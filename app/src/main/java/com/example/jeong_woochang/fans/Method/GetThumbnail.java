package com.example.jeong_woochang.fans.Method;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jeong-woochang on 2018. 8. 13..
 */

public class GetThumbnail extends AsyncTask<String, Void, ArrayList<String>> {
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        String url="https://fans.jype.com/"+strings.clone()[0];
        System.out.println("GetThumbnail:::url:::"+url);
        ArrayList<String> thumbnail=new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute();

            Document source = response.parse();
            Elements divContent = source.select("div[id=divContent]");
            Elements elements=divContent.get(0).select("img");
            for (org.jsoup.nodes.Element element : elements) {
                thumbnail.add(element.attr("src"));
                Log.d("GetThumbnail:::image:::",element.attr("src"));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return thumbnail;
    }
}
