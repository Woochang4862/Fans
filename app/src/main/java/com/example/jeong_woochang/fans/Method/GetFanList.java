package com.example.jeong_woochang.fans.Method;

import android.os.AsyncTask;
import android.widget.Adapter;

import com.example.jeong_woochang.fans.Adapter.DrawerAdapter;
import com.example.jeong_woochang.fans.Adapter.ListViewAdapter;
import com.example.jeong_woochang.fans.POJO.DrawerItem;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeong-woochang on 2018. 6. 21..
 */

public class GetFanList extends AsyncTask<Void, Void, DrawerAdapter> {

    @Override
    protected DrawerAdapter doInBackground(Void... voids) {

        DrawerAdapter menu_list_adapter=new DrawerAdapter();
        try {
            ArrayList<String[]> tmp= new ArrayList<>();
            // 로그인 페이지 접속
            Connection.Response loginPageResponse = Jsoup.connect("https://fans.jype.com/Default")
                    .timeout(3000)
                    .header("Origin", "https://fans.jype.com/")
                    .header("Referer", "https://fans.jype.com/Default")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .method(Connection.Method.GET)
                    .execute();

            Document loginPageDocument = loginPageResponse.parse();

            //Form Data for Token
            String __LASTFOCUS = loginPageDocument.select("#__LASTFOCUS").val();
            String __EVENTTARGET = loginPageDocument.select("#__EVENTTARGET").val();
            String __EVENTARGUMENT = loginPageDocument.select("#__EVENTARGUMENT").val();
            String __VIEWSTATE = loginPageDocument.select("#__VIEWSTATE").val();
            String __VIEWSTATEGENERATOR = loginPageDocument.select("#__VIEWSTATEGENERATOR").val();
            String __EVENTVALIDATION = loginPageDocument.select("#__EVENTVALIDATION").val();

            // Window, Chrome의 User Agent.
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

            // 전송할 폼 데이터
            Map<String, String> data = new HashMap<>();
            data.put("__LASTFOCUS", __LASTFOCUS);
            data.put("__EVENTTARGET", __EVENTTARGET);
            data.put("__EVENTARGUMENT", __EVENTARGUMENT);
            data.put("__VIEWSTATE", __VIEWSTATE); // 로그인 페이지에서 얻은 토큰들
            data.put("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR);
            data.put("__EVENTVALIDATION", __EVENTVALIDATION);
            data.put("txtUserID", "chad76");
            data.put("txtPassword", "164138");
            data.put("btnLogin", "LOGIN");


            System.out.println(data);

            Connection.Response loginPageResponse_for_cookies = Jsoup.connect("https://fans.jype.com/Default")
                    .timeout(3000)
                    .header("Origin", "https://fans.jype.com/")
                    .header("Referer", "https://fans.jype.com/Default")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .userAgent(userAgent)
                    .data(data)
                    .method(Connection.Method.POST)
                    .execute();

            // 로그인 성공 후 얻은 쿠키.
            Map<String, String> loginTryCookie = loginPageResponse_for_cookies.cookies();

            Connection.Response response = Jsoup.connect("https://fans.jype.com/MyFans")
                    .userAgent(userAgent)
                    .header("Origin", "https://fans.jype.com/")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .cookies(loginTryCookie)
                    .method(Connection.Method.GET)
                    .execute();


            Document source = response.parse();
            Elements elements = source.select(".col-lg-6");
            for (org.jsoup.nodes.Element element : elements) {
                org.jsoup.nodes.Element element1 = element.select("img[class=img-responsive center-block]").first();
                String name= element1.attr("src");
                element1 = element.select("img[class=img-responsive center-block]").get(1);
                String img = element1.attr("src");
                tmp.add(new String[]{name,img});
            }
            System.out.println("SIZE:"+tmp.size());
            for(int i=0;i<tmp.size();i++) {
                menu_list_adapter.addItem(tmp.get(i)[0], tmp.get(i)[1]);
            }
            menu_list_adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return menu_list_adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(DrawerAdapter drawerAdapter) {super.onPostExecute(drawerAdapter);}

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}