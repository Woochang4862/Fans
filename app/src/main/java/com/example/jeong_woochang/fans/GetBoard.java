package com.example.jeong_woochang.fans;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jeong_woochang.fans.Adapter.ListViewAdapter;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeong-woochang on 2018. 6. 21..
 */

public class GetBoard {

    //파싱한 정보를 받을 String 변수
    String num = "num", title = "title", name = "name", date = "date", view = "view", href = "href";

    //게시판 파싱
    public ListViewAdapter getItem(final Context context, final ListViewAdapter adapter, final String board_name, final String page, final String search_query, final String search_field) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Source source;
                try {
                    String parsing_url="https://fans.jype.com/BoardList?BoardName=" + board_name + "&SearchField="+search_field+"&SearchQuery="+search_query+"&Page="+page;
                    URL url = new URL(parsing_url);
                    source = new Source(url);

                    // date 가져오기

                    //모든 TABLE 태그를 가져와 리스트에 담음
                    List<Element> listTABLE = source.getAllElements(HTMLElementName.TABLE);
                    //System.out.println(listTABLE.size());
                    for (int i = 0; i < listTABLE.size(); i++) {
                        Element TABLE = listTABLE.get(i);

                        // TABLE태그의 id 값을 가져옴
                        String TABLE_id = TABLE.getAttributeValue("id");
                        if (TABLE_id != null) {

                            // id 값이 MainContent_CenterContent_ctlBoardListPrivate 이면
                            if (TABLE_id.equalsIgnoreCase("MainContent_CenterContent_ctlBoardListPrivate")) {

                                //TABLE태그의 id 값이 MainContent_CenterContent_ctlBoardListPrivate 인 Element에서 모든 TR태그를 가져옴
                                List<Element> listTR = TABLE.getAllElements(HTMLElementName.TR);

                                for (int j = 0; j < listTR.size(); j++) {

                                    //모든 TR태크 중에서 j 번째 TR 태그를 가져옴
                                    Element TR = listTR.get(j);

                                    //j번째 TR태그에서 모든 TD태그를 가져옴
                                    List<Element> listTD = TR.getAllElements(HTMLElementName.TD);

                                    for (int l = 0; l < listTD.size(); l++) {

                                        // 모근 TD태그 중에서 l 번째 TD 태그를 가져옴
                                        Element TD = listTD.get(l);

                                        //num title name date view 순으로 되있으므로 l값에 따라 값을 저장
                                        switch (l) {
                                            case 0:
                                                num = TD.getTextExtractor().toString();
                                                break;
                                            case 1:
                                                List<Element> listA = TD.getAllElements(HTMLElementName.A);
                                                Element A = listA.get(0);
                                                href = A.getAttributeValue("href");
                                                title = TD.getTextExtractor().toString();
                                                break;
                                            case 2:
                                                name = TD.getTextExtractor().toString();
                                                break;
                                            case 3:
                                                date = TD.getTextExtractor().toString();
                                                break;
                                            case 4:
                                                view = TD.getTextExtractor().toString();
                                                break;
                                        }
                                    }
                                    //공백을 없애줌
                                    // num 가공
                                    num = num.replace("\n" +
                                            "                                        ", "");
                                    num = num.replace("\n" +
                                            "                                    ", "");
                                    //name 가공
                                    name = name.replace("\n" +
                                            "                                        ", "");
                                    name = name.replace("\n" +
                                            "                                    ", "");
                                    //date 가공
                                    date = date.replace("\n" +
                                            "                                        ", "");
                                    date = date.replace("\n" +
                                            "                                    ", "");

                                    //아이템 추가
                                    adapter.addItem(num, title, name, date, view, href);
                                }//for 2
                            }//if 2
                        }//if 1
                    }//for 1

                } catch (Exception e) {
                    Log.d("error :::: ", e.getMessage());
                }
            }//run()
        };//Runnalble

        final View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);

        Thread thread = new Thread(runnable);
        try {
            thread.start();
            thread.join();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    ProgressBar progressBar=(ProgressBar)rootView.findViewById(R.id.progressbar);
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity)context).setmLockListView(false);
                }
            }, 1000);
        } catch (Exception e) {

        }
        return adapter;
    }

}
