package com.github.bysky.charmplayer;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import static com.github.bysky.charmplayer.BaseActivity.musicSQLiteDatabases;

public class ScanFileService extends BaseService
    implements Runnable{

    public final int SCAN_RUN = 1;
    public final int SCAN_STOP = 0;
    private int maxDepth;
    private long lengthLimit;  //默认大于64KB的歌曲
    private ArrayList<String> arry_selected_path;
    private ArrayList<File> arry_music_file;
    private ScanBinder binder;
    private Thread thread;  //标记扫描对象
    private int scanFlag;   //作为服务起始/停止的依据
    private ScanFileForMusicActivity.ScanHandler handler;

    /**
     *  服务控制类
     * */
    public class ScanBinder extends Binder{

        public void startScan(ArrayList<String> scanList, ScanFileForMusicActivity.ScanHandler handler, int maxFolderDepth, int fileMinLength){
            mainScan(scanList,handler,maxFolderDepth,fileMinLength);
        }

        public String getProgress(){
            return null;
        }

        public void stopScan(){
            scanFlag = SCAN_STOP;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        scanFlag = SCAN_RUN;
        if(binder == null)
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
        return Service.START_NOT_STICKY;
    }

    private void mainScan(ArrayList<String> pathList, ScanFileForMusicActivity.ScanHandler handler, int maxFolderDepth, int fileMinLength_KB){
        //准备工作
        this.handler = handler;
        arry_selected_path = pathList;
        setMinLen(fileMinLength_KB);
        if(maxFolderDepth < 1 ){
            Log.w(".ScanFileService","the param import is illegal(maxFolderDepth < 1)");
            return;
        }else
            maxDepth = maxFolderDepth;
        //开始扫描
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        arry_music_file = new ArrayList<File>();
        for(String path:arry_selected_path){
            if(!isAllowScan())
                break;
            loopFileTree(arry_music_file,new File[]{new File(path)},1 );
        }
        //查询入库
        updateDatabase(arry_music_file);
        //返回结果
        Message message = new Message();
        if(isAllowScan()){
            message.what = ScanFileForMusicActivity.ScanHandler.SCAN_FINISH;
            Log.w("ScanFileService","run()-> Scan has finished !");
        }else
            message.what = ScanFileForMusicActivity.ScanHandler.SCAN_STOP;
        handler.sendMessage(message);
    }

    private void loopFileTree(ArrayList<File> pathList, File[] fileTree, int treeDepth){
        if(treeDepth > maxDepth || !isAllowScan())//不允许继续搜索
            return;
        //根据允许搜索的目录，执行搜索任务
        for(File file:fileTree){
            //遍历搜索
            if(!file.isDirectory()){
                //判断是否为音乐文件，是则加入arry
                String suffix = file.getName().substring(file.getName().lastIndexOf(".")+1);
                if(suffix.equalsIgnoreCase("mp3")||suffix.equalsIgnoreCase("wav")||suffix.equalsIgnoreCase("arm")
                        ||suffix.equalsIgnoreCase("mid"))
                    pathList.add(file);
            }else
                loopFileTree(pathList,file.listFiles(),treeDepth+1);
        }
    }

    private void updateDatabase(ArrayList<File> fileList){
        ContentValues contentValues = new ContentValues();    //保存插入数据集
        String music_name,artist;   //保存文件信息
        ArrayList<String> dataList = new ArrayList<String>();
        Cursor cursor = musicSQLiteDatabases.query("MUSIC",new String[]{"FILE_PATH"}
            ,null,null,null,null,null);

        //将之前已保存的歌曲放入dataList用于比较
        if(cursor.moveToFirst()){
            do {
                if(!isAllowScan())
                    return; //不允许搜索
                dataList.add(cursor.getString(cursor.getColumnIndex("FILE_PATH")));
            }while (cursor.moveToNext());
        }

        //通过比较找出那些新增的歌曲并入库
        for(File file:fileList){
            if(!isAllowScan())
                return; //不允许继续搜索

            if(file.length() < lengthLimit || dataList.contains(file.getAbsolutePath()))
                continue;
            //未包含于曲库说明是新歌曲（且大于大小限制）
            try{
                music_name = artist = null;
                String fileName = file.getAbsolutePath().substring( file.getAbsolutePath().lastIndexOf('/')+1
                        ,file.getAbsolutePath().lastIndexOf('.') ) ;
                String[] strings = getArtistAndMusic(fileName);
                artist = strings[0];
                music_name = strings[1];
                if(strings[0].equals("未知歌手") &&
                        file.getName().substring(file.getName().lastIndexOf(".")+1).equalsIgnoreCase("mp3")){
                    //歌手
                    String temp = getInnerArtist(file);
                    if(temp.length()>=1 && !temp.equals(" ") &&!temp.matches(".{3,}"))
                        artist = temp;
                }
                //放入信息集
                contentValues.clear();
                contentValues.put("FILE_PATH",file.getAbsolutePath());
                contentValues.put("FILE_NAME",fileName);
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
     * 注意：内部不对后缀名进行检查，调用者必须保证为mp3格式文件
     * */
    private String getInnerMusicName(File file) throws IOException{
        RandomAccessFile randaf = new RandomAccessFile(file,"r");        //文件定位读取
        String name;
        //读取信息(歌曲信息存放于末尾的128个字节中)
        randaf.seek(file.length()-125);
        //将RandomAccessFile读取出的“ISO-8859-1”编码的文字转换为“GBK”
        name = randaf.readLine();
        int temp = name.indexOf('\0');
        if(temp!=-1)
            name = new String(name.substring(0,temp).getBytes("ISO-8859-1") , "GBK");
        else if(name.length()>30)
            name = new String(name.substring(0,30).getBytes("ISO-8859-1") , "GBK"); //歌曲名
        else if(name.length()<=30)
            name = new String(name.getBytes("ISO-8859-1") , "GBK");
        randaf.close();
        return name;
    }

    private String getInnerArtist(File file) throws IOException{
        RandomAccessFile randaf = new RandomAccessFile(file,"r");        //文件定位读取
        String artist;
        randaf.seek(file.length()-95);
        artist = randaf.readLine();
        int temp = artist.indexOf('\0');
        if(temp!=-1)
            artist = new String(artist.substring(0,temp).getBytes("ISO-8859-1"),"GBK");
        else if(artist.length()>30)
            artist = new String(artist.substring(0,30).getBytes("ISO-8859-1"),"GBK");
        else if(artist.length()<=30)
            artist = new String( artist.getBytes("ISO-8859-1") ,"GBK" );
        randaf.close();
        return  artist;
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
    private boolean isAllowScan(){ return scanFlag==SCAN_RUN;}
}
