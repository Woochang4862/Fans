package com.example.jeong_woochang.fans.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeong_woochang.fans.POJO.DrawerItem;
import com.example.jeong_woochang.fans.R;

import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 5. 16..
 */

public class DrawerAdapter extends BaseAdapter {

    ArrayList<DrawerItem> arrayList = new ArrayList<DrawerItem>();

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context=parent.getContext();

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.drawer_item,parent,false);
        }

        ImageView groupImg=(ImageView)convertView.findViewById(R.id.group_img);
        ImageView groupName=(ImageView)convertView.findViewById(R.id.name);

        DrawerItem drawerItem=arrayList.get(position);

        groupImg.setImageURI(Uri.parse(drawerItem.getImg()));
        groupName.setImageURI(Uri.parse(drawerItem.getName()));

        return convertView;
    }

    public void addItem(DrawerItem item) {
        arrayList.add(item);
    }

    public void clear() {
        arrayList.clear();
    }
}
