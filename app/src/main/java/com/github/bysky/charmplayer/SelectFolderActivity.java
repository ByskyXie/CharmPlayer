package com.github.bysky.charmplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SelectFolderActivity extends BaseActivity
            implements View.OnClickListener{

    private Toolbar toolbar;
    private String preDir;
    private String rootDir;
    private SelectFolderAdapter.OnItemClickListener listener;
    private ArrayList<String> dirList;
    private RecyclerView recyclerSelect;
    private TextView textViewShowPath;
    private Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        toolbar = (Toolbar)findViewById(R.id.toolbar_select_folder);
        toolbar.setTitle("请选择目录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TODO:增加全选框
        //初始化控件
        initialUI();
        //设置布局
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        SelectFolderAdapter adapter = new SelectFolderAdapter(this,dirList,listener,llm);
        recyclerSelect.setLayoutManager(llm);
        recyclerSelect.setAdapter(adapter);
        recyclerSelect.addItemDecoration(new DividerItemDecoration(this,LinearLayout.VERTICAL));
    }

    @Override
    protected void initialUI(){
        buttonConfirm = (Button) findViewById(R.id.button_select_folder_confirm);
        //一开始不能点确定
        setButtonUnable(buttonConfirm);
        buttonConfirm.setOnClickListener(this);
        textViewShowPath = (TextView)findViewById(R.id.text_view_show_path);
        textViewShowPath.setOnClickListener(this);
        rootDir = preDir =  Environment.getExternalStorageDirectory().toString();
        refreshFolderPath();
        dirList = getDirList();

        //子项目点击监听器
        listener = new SelectFolderAdapter.OnItemClickListener() {
            @Override
            public void onClick(Context context,String path,SelectFolderAdapter adapter) {
                SelectFolderActivity activity = (SelectFolderActivity)context;
                RecyclerView recycler = activity.recyclerSelect;

                //回到根目录或点击文件则无响应
                if(!path.contains(activity.getRootDir()) || !new File(path).isDirectory())
                    return;

                //重新设置已选中的目录数并将check标记置空
                cancelSelectedMarks(recycler.getLayoutManager());

                //改变根目录
                activity.setPreDir(path);
                activity.refreshFolderPath();

                //更新选项
                ((SelectFolderAdapter)recycler.getAdapter()).setList(activity.getDirList());
                recycler.getAdapter().notifyDataSetChanged();
            }
        };
        recyclerSelect = findViewById(R.id.recycler_select_folder);
        textViewShowPath = findViewById(R.id.text_view_show_path);
        //TODO：当选中了目录才能点确定扫描
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_view_show_path:
                String path = textViewShowPath.getText().toString()
                        .substring(0, textViewShowPath.getText().toString().lastIndexOf('/'));
                listener.onClick(this, path,(SelectFolderAdapter) recyclerSelect.getAdapter());
                break;
            case R.id.button_select_folder_confirm:
                //启动扫描服务
                Intent intent = new Intent(this,ScanFileForMusicActivity.class);
                //设置最小文件大小
                intent.putStringArrayListExtra("scanList"
                        ,getSelectedFolder((SelectFolderAdapter) recyclerSelect.getAdapter()));
                //设置搜索深度
                if(((CheckBox)findViewById(R.id.checkbox_deep_scan)).isChecked())
                    intent.putExtra("maxFolderDepth",0xfffffff);//TODO:0xfffffff近似全局扫描
                else
                    intent.putExtra("maxFolderDepth",4);
                intent.putExtra("fileMinLength",64);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取已选择的目录
     * */
    protected ArrayList<String> getSelectedFolder(SelectFolderAdapter adapter){
        ArrayList<String> selectedList = new ArrayList<String>();
//        由于控件复用的原因，不可用chileCount用来定位元素个数
        ArrayList<String> pathList = adapter.getList();
        boolean[] itemArray = adapter.getIsCheckedArray();
        for(int i=0;i<itemArray.length;i++){
            if(itemArray[i])
                selectedList.add(preDir+"/"+pathList.get(i));
        }
        return selectedList;
    }

    protected void cancelSelectedMarks(RecyclerView.LayoutManager layoutManager){
        int count = layoutManager.getChildCount();
        for(int i=0;i<count;i++){
            View view = layoutManager.getChildAt(i);
            ((CheckBox)view.findViewById(R.id.checkBox_item_select_folder)).setChecked(false);
        }
    }

    protected void refreshFolderPath(){
        textViewShowPath.setText(preDir);
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

    public void refreshConfirmButtonState(boolean haveSelectedItems){
        if(haveSelectedItems)
            setButtonEnable(buttonConfirm);
        else
            setButtonUnable(buttonConfirm);
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
