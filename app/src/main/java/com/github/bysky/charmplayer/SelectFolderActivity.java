package com.github.bysky.charmplayer;

import android.content.Context;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class SelectFolderActivity extends BaseActivity
            implements View.OnClickListener{
    private Toolbar toolbar;
    private String preDir;
    private String rootDir;
    private SelectFolderAdapter.OnItemClickListener listener;
    private ArrayList<String> dirList;
    private RecyclerView recycler_select;
    private TextView textView_showPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        toolbar = (Toolbar)findViewById(R.id.toolbar_select_folder);
        toolbar.setTitle("请选择目录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化控件
        initialUI();
        //设置布局
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        SelectFolderAdapter adapter = new SelectFolderAdapter(this,dirList,listener,llm);
        recycler_select.setLayoutManager(llm);
        recycler_select.setAdapter(adapter);
    }

    @Override
    protected void initialUI(){
        textView_showPath = (TextView)findViewById(R.id.text_view_show_path);
        textView_showPath.setOnClickListener(this);
        rootDir = preDir =  Environment.getExternalStorageDirectory().toString();
        refreshFolderPath();
        dirList = getDirList();

        listener = new SelectFolderAdapter.OnItemClickListener() {
            @Override
            public void onClick(Context context,String path) {
                SelectFolderActivity activity = (SelectFolderActivity)context;
                if(!path.contains(activity.getRootDir()))
                    return;
                File file = new File(path);
                if(!file.isDirectory())
                    return; //点击文件则无响应

                activity.setPreDir(path);  //改变根目录
                activity.refreshFolderPath();

                ((SelectFolderAdapter)activity.recycler_select.getAdapter())
                        .setList(activity.getDirList());//getDirList为空则清空选项
                activity.recycler_select.getAdapter().notifyDataSetChanged();
            }
        };
        recycler_select = (RecyclerView) findViewById(R.id.recycler_select_folder);
        textView_showPath = (TextView)findViewById(R.id.text_view_show_path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_view_show_path:
                String path = textView_showPath.getText().toString()
                        .substring(0, textView_showPath.getText().toString().lastIndexOf('/'));
                listener.onClick(this, path);
                break;
            case R.id.button_select_folder_confirm:
                ArrayList<String> scanList = new ArrayList<String>();

                break;
        }
    }

    protected void refreshFolderPath(){
        textView_showPath.setText(preDir);
    }

    private ArrayList<String> getDirList(){
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(preDir);
        if(!file.exists()){ //报错，路径不存在
            Log.e(".SelectFolderActivity","path preDir not exist!!!\n");
            return list;
        }
        String[] fileList = file.list();
        for(String s:fileList){
            //若为dir或音频文件则加入
            String suffix = null;
            file = new File(preDir+"/"+s);
            if(s.lastIndexOf('.')!=-1)
                suffix = s.substring( s.lastIndexOf('.')+1 , s.length());
            if(file.isDirectory() || //是文件夹，或者是音频文件
                    (suffix!=null && (suffix.equalsIgnoreCase("mp3")||suffix.equalsIgnoreCase("wav")
                     ||suffix.equalsIgnoreCase("arm")||suffix.equalsIgnoreCase("mid"))  ) )
                list.add(s);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        //TODO:当不能返回上一层文件夹时调用super
        super.onBackPressed();
    }

    public String getPreDir(){
        return preDir;
    }
    public void setPreDir(String dir){
        preDir = dir;
    }
    public SelectFolderAdapter.OnItemClickListener getListener() {
        return listener;
    }

    public String getRootDir() {
        return rootDir;
    }
}
