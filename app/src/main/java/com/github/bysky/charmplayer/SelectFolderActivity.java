package com.github.bysky.charmplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SelectFolderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String preDir;
    private ArrayList<String> dirList;
    private RecyclerView recycler_select;
    private TextView textView_showPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        toolbar = (Toolbar)findViewById(R.id.toolbar_select_folder);
        toolbar.setTitle("自定义扫描");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //初始化控件
        textView_showPath = (TextView)findViewById(R.id.text_view_show_path);
        preDir =  Environment.getExternalStorageDirectory().toString();
        textView_showPath.setText(preDir);
        dirList = getDirList();
        //设置布局
        SelectFolderAdapter.OnItemClickListener listener = new SelectFolderAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, ArrayList<String> list) {
                String s = list.get(position);
                //TODO:判断点击的是文件还是文件夹并对应操作,注意及时修改dirList
            }
        };
        recycler_select = (RecyclerView) findViewById(R.id.recycler_select_folder);
        textView_showPath = (TextView)findViewById(R.id.text_view_show_path);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        SelectFolderAdapter adapter = new SelectFolderAdapter(this,dirList,listener,llm);
        recycler_select.setLayoutManager(llm);
        recycler_select.setAdapter(adapter);
    }

    public ArrayList<String> getDirList(){
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(preDir);
        if(!file.exists()){ //报错，路径不存在
            Log.e(".SelectFolderActivity","path preDir not exist!!!\n");
            return list;
        }
        String[] fileList = file.list();
        for(String s:fileList){
            //若为dir或音频文件则加入
            list.add(s);
        }
        return list;
    }

    public String getPreDir(){
        return preDir;
    }
}
