package com.example.jeong_woochang.fans.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeong_woochang.fans.POJO.ListVIewItem;
import com.example.jeong_woochang.fans.R;

import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 7. 23..
 */

public class RecyclerView extends android.support.v7.widget.RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ListVIewItem> list=new ArrayList<>();

    public RecyclerView(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item,parent,false);
        ViewHolder vh=new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListVIewItem item=list.get(position);
        TextView title =(TextView)holder.layout.findViewById(R.id.title);
        TextView num =(TextView)holder.layout.findViewById(R.id.num);
        TextView name =(TextView)holder.layout.findViewById(R.id.name);
        TextView view =(TextView)holder.layout.findViewById(R.id.view);
        TextView date =(TextView)holder.layout.findViewById(R.id.date);

        title.setText(item.getTitle());
        num.setText(item.getNum());
        name.setText(item.getName());
        view.setText(item.getView());
        date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public LinearLayout layout;
        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.layout=layout;
        }
    }
}
