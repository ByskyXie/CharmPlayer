package com.github.bysky.charmplayer;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by asus on 2017/11/15.
 */

public class SelectFolderAdapter extends RecyclerView.Adapter<SelectFolderAdapter.FolderItemHolder> {

    private Context context;
    private ArrayList<String> list;
    private RecyclerView.LayoutManager llm;
    private OnItemClickListener listener;

    static class FolderItemHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private TextView textView;
        private ImageView img;
        private View parent;
        FolderItemHolder(View itemView) {
            super(itemView);
            parent = itemView;
            checkBox = itemView.findViewById(R.id.checkBox_item_select_folder);
            textView = itemView.findViewById(R.id.textView_item_select_folder);
            img = itemView.findViewById(R.id.img_item_select_folder);
        }
    }

    SelectFolderAdapter(Context context, ArrayList<String> dirList
            , OnItemClickListener listener, RecyclerView.LayoutManager llm){
        this.llm = llm;
        this.list = dirList;
        this.context = context;
        this.listener = listener;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(FolderItemHolder holder, final int position) {
        String dir = list.get(position);
        final String preDir = ((SelectFolderActivity)context).getPreDir();
        File file = new File(preDir+"/"+dir);
        holder.textView.setText(dir);
        int temp = holder.img.getPaddingTop();
        if(file.isFile())  //是文件则改变图标
            holder.img.setBackgroundResource(R.drawable.ic_file);
        else
            holder.img.setBackgroundResource(R.drawable.ic_folder);
        holder.img.setPadding(0,temp,0,temp);
        //TODO:check监听器记录

        //监听器
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(context,preDir+"/"+list.get(position));
            }
        });
        //改变logo长宽+改变控件宽度
        int w = llm.getWidth()/7;
        ViewGroup.LayoutParams params = holder.img.getLayoutParams();
        params.height = params.width = w;
    }

    @Override
    public FolderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_select_folder,parent,false);
        FolderItemHolder holder = new FolderItemHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    interface OnItemClickListener{
        void onClick(Context context,String path);
    }
}
