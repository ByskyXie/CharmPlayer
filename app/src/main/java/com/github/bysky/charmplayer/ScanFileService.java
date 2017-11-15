package com.github.bysky.charmplayer;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import static com.github.bysky.charmplayer.BaseActivity.musicSQLiteDatabases;

public class ScanFileService extends Service {

    public final int SCAN_RUN = 1;
    public final int SCAN_STOP = 0;
    private int scan_statue;
    private int maxDepth;
    private long lengthLimit;  //默认大于64KB的歌曲
    private ArrayList<String> arry_selected_path;
    private ArrayList<File> arry_music_file;
    private ScanBinder binder;

    public class ScanBinder extends Binder{
        public void startScan(ArrayList<String> list, int maxFolderDepth, int fileMinLength){
            arry_selected_path = list;
            mainScan(arry_selected_path,maxFolderDepth,fileMinLength);
        }
        public void getProgress(){

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        scan_statue = SCAN_RUN;
        if(binder==null)
            binder = new ScanBinder();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new ScanBinder();
    }

    @Override
    public int onStartCommand(Intent intent, /**@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)*/ int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //每次启用服务前的准备
        Log.w(".ScanFileService","Standard Service start( startService(intent) )");
        arry_selected_path = intent.getBundleExtra("selectedFolder").getStringArrayList("folder");
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void mainScan(ArrayList<String> path, int maxFolderDepth, int fileMinLength_KB){
        File file;
        setMinLen(fileMinLength_KB);
        if(maxFolderDepth < 1 ){
            Log.w(".ScanFileService","the param import is illegal(maxFolderDepth < 1)");
            return;
        }else
            maxDepth = maxFolderDepth;

        arry_music_file = new ArrayList<File>();
        //迭代检索
        for(String s:path){
            if(!checkState())
                return;//不允许继续搜索
            file = new File(s);
            loopFileTree(arry_music_file,new File[]{file},1);
        }
        //查询入库
        updateDatabase(arry_music_file);
        Log.w("ScanFileService","mainScan()-> Scan has finished !!!!!!!");
    }
    private void loopFileTree(ArrayList<File> arry,File[] fileTree,int treeDepth){
        //调试用，使用前要拿掉
        if(treeDepth > maxDepth)
            return;
        //根据允许搜索的目录，执行搜索任务
        for(File file:fileTree){

            if(!checkState())
                return;//不允许继续搜索

            //遍历搜索
            if(!file.isDirectory()){
                //判断是否为音乐文件，是则加入arry
                String suffix = file.getName().substring(file.getName().lastIndexOf(".")+1);
                if(suffix.equalsIgnoreCase("mp3")||suffix.equalsIgnoreCase("wav")||suffix.equalsIgnoreCase("arm")
                        ||suffix.equalsIgnoreCase("mid"))
                    arry.add(file);
            }else
                loopFileTree(arry,file.listFiles(),treeDepth+1);
        }
    }
    private void updateDatabase(ArrayList<File> fileList){
        ContentValues contentValues = new ContentValues();    //保存插入数据集
        String music_name,artist; //保存文件信息
        RandomAccessFile randaf;        //文件定位读取
        ArrayList<String> dataList = new ArrayList<String>();
        Cursor cursor = musicSQLiteDatabases.query("MUSIC",new String[]{"FILE_PATH"},null,null,null,null,null);
        //将曲库中的歌曲放入dataList便于比较
        if(cursor.moveToFirst())
            do dataList.add(cursor.getString(cursor.getColumnIndex("FILE_PATH")));
            while (cursor.moveToNext());
        //逐一检查
        for(File file:fileList){

            if(!checkState())
                return;//不允许继续搜索

            if(file.length()<lengthLimit || dataList.contains(file.getAbsolutePath()))
                continue;
            //未包含于曲库说明是新歌曲（且大于大小限制）
            try{
                music_name = artist = null;
                if(file.getName().substring(file.getName().lastIndexOf(".")+1).equalsIgnoreCase("mp3")){
                    //MP3格式有额外的歌曲信息可以入库
                    randaf = new RandomAccessFile(file,"r");
                    //读取信息(歌曲信息存放于末尾的128个字节中)
                    randaf.seek(file.length()-125);
                    //将RandomAccessFile读取出的“ISO-8859-1”编码的文字转换为“UTF-8”
                    music_name = randaf.readLine();
                    int temp = music_name.indexOf('\0');
                    if(temp!=-1)
                        music_name = new String(music_name.substring(0,temp).getBytes("ISO-8859-1") , "GBK");
                    else if(music_name.length()>30)
                        music_name = new String(music_name.substring(0,30).getBytes("ISO-8859-1") , "GBK"); //歌曲名
                    else if(music_name.length()<=30)
                        music_name = new String(music_name.getBytes("ISO-8859-1") , "GBK");
                    //接下来是歌手(同上)
                    randaf.seek(file.length()-95);
                    artist = randaf.readLine();
                    temp = artist.indexOf('\0');
                    if(temp!=-1)
                        artist = new String(artist.substring(0,temp).getBytes("ISO-8859-1"),"GBK");
                    else if(artist.length()>30)
                        artist = new String(artist.substring(0,30).getBytes("ISO-8859-1"),"GBK");
                    else if(artist.length()<=30)
                        artist = new String( artist.getBytes("ISO-8859-1") ,"GBK" );
                }
                //放入信息集
                contentValues.clear();
                contentValues.put("FILE_PATH",file.getAbsolutePath());
                contentValues.put("FILE_NAME",file.getAbsolutePath().substring( file.getAbsolutePath().lastIndexOf('/')+1
                        ,file.getAbsolutePath().lastIndexOf('.') ) );
                contentValues.put("FILE_FOLDER",file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf('/')));
                contentValues.put("MUSIC_NAME",music_name);
                contentValues.put("ARTIST",artist);
                //入库
                musicSQLiteDatabases.insert("MUSIC",null,contentValues);
            }catch(FileNotFoundException ffe){
                Log.e(".ScanFileService","insert to database case found file failed\n"+ffe);
            }catch(IOException ioe){
                Log.e(".ScanFileService","insert to database case read file failed\n"+ioe);
            }
        }
    }
    /**
     * 设置最小扫描文件
     * */
    private void setMinLen(long valueKB){
        lengthLimit = valueKB*1024;
    }
    /**
     * 检查搜索状态
     * */
    private boolean checkState(){ return scan_statue==SCAN_RUN;}
}