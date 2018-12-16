package com.example.jeong_woochang.fans.Method;

import android.content.Context;
import android.util.Log;

import com.example.jeong_woochang.fans.POJO.ParsingItem;

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

    private ArrayList<ParsingItem> mList=new ArrayList<>();

    //게시판 파싱
    public ArrayList<ParsingItem> getItem(final Context context, final String board_name, final String page, final String search_query, final String search_field) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Source source;
                try {
                    String parsing_url = "https://fans.jype.com/BoardList?BoardName=" + board_name + "&SearchField=" + search_field + "&SearchQuery=" + search_query + "&Page=" + page;
                    System.out.println(parsing_url);
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

                                    ParsingItem item=new ParsingItem();
                                    for (int l = 0; l < listTD.size(); l++) {

                                        // 모근 TD태그 중에서 l 번째 TD 태그를 가져옴
                                        Element TD = listTD.get(l);

                                        //num title name date view 순으로 되있으므로 l값에 따라 값을 저장
                                        switch (l) {
                                            case 0:
                                                item.setNum(TD.getTextExtractor().toString().replace("\n" +
                                                        "                                        ", ""));
                                                break;
                                            case 1:
                                                List<Element> listA = TD.getAllElements(HTMLElementName.A);
                                                Element A = listA.get(0);
                                                item.setHref(A.getAttributeValue("href").replace("\n" +
                                                        "                                        ", ""));
                                                item.setTitle(TD.getTextExtractor().toString().replace("\n" +
                                                        "                                        ", ""));
                                                item.setThumbnail(new GetThumbnail().execute(A.getAttributeValue("href").replace("\n" +
                                                        "                                        ", "")).get());
                                                break;
                                            case 2:
                                                item.setName(TD.getTextExtractor().toString().replace("\n" +
                                                        "                                        ", ""));
                                                break;
                                            case 3:
                                                item.setDate(TD.getTextExtractor().toString().replace("\n" +
                                                        "                                        ", ""));
                                                break;
                                            case 4:
                                                item.setView(TD.getTextExtractor().toString().replace("\n" +
                                                        "                                        ", ""));
                                                break;
                                        }
                                    }
                                    //아이템 추가
                                    mList.add(item);
                                }//for 2
                            }//if 2
                        }//if 1
                    }//for 1

                } catch (Exception e) {
                    Log.d("error :::: ", e.getMessage());
                }
            }//run()
        };//Runnalble

        Thread thread = new Thread(runnable);
        try {
            thread.start();
            thread.join();
            Log.d("###Parsed Data###", toString(mList));
            return mList;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString(ArrayList<ParsingItem> list){
        String results = "+";
        for(ParsingItem item : list) {
            results += item.toString() + ",";
        }
        return results;
    }

}
