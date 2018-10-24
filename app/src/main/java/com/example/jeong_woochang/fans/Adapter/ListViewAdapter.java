package com.example.jeong_woochang.fans.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeong_woochang.fans.POJO.ListVIewItem;
import com.example.jeong_woochang.fans.R;

import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 1. 24..
 */

public class ListViewAdapter extends BaseAdapter {
    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    public ArrayList<ListVIewItem> listViewItems = new ArrayList<ListVIewItem>();
    public ArrayList<String[]> sumnails = new ArrayList<>();

    // ListViewAdapter의 생성자
    public ListViewAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItems.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView numTextView = (TextView) convertView.findViewById(R.id.num);
        ViewPager sumbnails = (ViewPager) convertView.findViewById(R.id.sumbnails);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView viewTextView = (TextView) convertView.findViewById(R.id.view);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListVIewItem listViewItem = listViewItems.get(pos);

        // 아이템 내 각 위젯에 데이터 반영
        numTextView.setText(listViewItem.getNum());
        sumbnails.setAdapter(new ScreenSlidePagerAdapter(((FragmentActivity) context).getSupportFragmentManager()));
        titleTextView.setText(listViewItem.getTitle());
        nameTextView.setText(listViewItem.getName());
        dateTextView.setText(listViewItem.getDate());
        viewTextView.setText(listViewItem.getView());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String num, String title, String name, String date, String view, String href) {
        ListVIewItem item = new ListVIewItem();

        item.setNum(num);
        item.setTitle(title);
        item.setName(name);
        item.setDate(date);
        item.setView(view);
        item.setHref(href);

        listViewItems.add(item);

    }

    public void addSumnail(ArrayList<String> sumnail) {
        sumnails.add(sumnail.toArray(new String[sumnail.size()]));
    }

    public void clear() {
        listViewItems.clear();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new SumnailFragment();
        }

        @Override
        public int getCount() {
            return sumnails.size();
        }
    }
}
