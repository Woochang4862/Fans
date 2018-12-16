package com.example.jeong_woochang.fans.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jeong_woochang.fans.POJO.DrawerItem;
import com.example.jeong_woochang.fans.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

import okio.Utf8;

/**
 * Created by jeong-woochang on 2018. 5. 16..
 */

public class DrawerAdapter extends BaseAdapter {

    public ArrayList<DrawerItem> arrayList = new ArrayList<>();

    public DrawerAdapter() {

    }

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
        Log.d("getView Count=", String.valueOf(position));
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);
        }

        DrawerItem drawerItem = arrayList.get(position);
        System.out.println(drawerItem.getImg() + " " + drawerItem.getName());

        /*TextView textView=(TextView)convertView.findViewById(R.id.test);
        textView.setText(drawerItem.getName());*/

        System.out.println(drawerItem.getImg());
        Glide.with(context)
                .load(Uri.parse(drawerItem.getImg()).buildUpon().appendQueryParameter("key", "val").build().toString())
                .into((ImageView) convertView.findViewById(R.id.group_img));

        System.out.println(drawerItem.getName());
        Glide.with(context)
                .load(Uri.parse(drawerItem.getName()).buildUpon().appendQueryParameter("key", "val").build().toString())
                .into((ImageView) convertView.findViewById(R.id.name));

        return convertView;
    }

    public void setArrayList(ArrayList<DrawerItem> arrayList){
        this.arrayList=arrayList;

    }

    public void clear() {
        arrayList.clear();
    }

    public String getBoardName(int pos) {
        return arrayList.get(pos).getBoardName();
    }

    public int getBoardNumber(String board_name) {
        for(DrawerItem item:arrayList){
            if(item.getBoardName()==board_name)
                return arrayList.indexOf(item);
        }
        return -1;
    }
}
