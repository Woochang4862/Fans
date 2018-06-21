package com.example.jeong_woochang.fans;

import android.os.AsyncTask;
import android.widget.Adapter;

import com.example.jeong_woochang.fans.Adapter.DrawerAdapter;
import com.example.jeong_woochang.fans.Adapter.ListViewAdapter;
import com.example.jeong_woochang.fans.POJO.DrawerItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 6. 21..
 */

public class GetFanList extends AsyncTask<Void,Void,DrawerAdapter> {


    @Override
    protected DrawerAdapter doInBackground(Void... voids) {
        DrawerAdapter adapter=new DrawerAdapter();
        ArrayList<DrawerItem> list=new ArrayList<DrawerItem>();

        try {
            Document source= Jsoup.connect("https://fans.jype.com/MyFans").get();
            Elements elements=source.select(".col-lg-6 #fanslist");
            for(Element element: elements){
                DrawerItem temp=new DrawerItem();

                Element element1=element.select("img[class=img-responsive center-block]").first();
                temp.setImg(element1.attr("src"));
                element1=element.select("img[class=img-responsive center-block]").first();
                temp.setName(element1.attr("src"));

                adapter.addItem(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(DrawerAdapter drawerAdapter) {
        super.onPostExecute(drawerAdapter);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
