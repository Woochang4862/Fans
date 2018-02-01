package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //ListView 객체
    ListView board;
    //list와 listview를 연결할 Adapter
    ListViewAdapter adapter;
    //파싱한 정보를 받을 String 변수
    String num="num",title="title",name="name",date="date",view="view", href="href";
    ArrayList<String> listHref;
    //파싱할 URL
    String parsing_url;
    //String형 page 및 Integer형 page
    String page="1";
    int page_int=1;
    //ImageView 객체=>page up, page down
    ImageView up,down;
    //TextView 객체=>page
    TextView pageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //url 지정
        parsing_url = "https://fans.jype.com/BoardList?BoardName=twice_notice";

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        board = (ListView) findViewById(R.id.board);
        board.setAdapter(adapter);

        //page_up,down 버튼 지정 및 PageView 지정
        up=(ImageView)findViewById(R.id.page_up);
        down=(ImageView)findViewById(R.id.page_down);
        pageView=(TextView)findViewById(R.id.page);

        //pageView를 page기본값(1)으로 지정
        pageView.setText(page);

        //추가 링크 정보를 담을 ArrayList 생성
        listHref=new ArrayList<String>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Source source;
                try {
                    URL url = new URL(parsing_url+"&Page="+page);
                    source = new Source(url);

                    // date 가져오기

                    // 모든 TABLE 태그를 가져와 리스트에 담음
                    List<Element> listTABLE = source.getAllElements(HTMLElementName.TABLE);

                    for (int i = 0; i < listTABLE.size(); i++) {
                        Element TABLE = listTABLE.get(i);

                        // TABLE태그의 id 값을 가져옴
                        String TABLE_id = TABLE.getAttributeValue("id");
                        if (TABLE_id != null) {

                            // id 값이 MainContent_CenterContent_ctlBoardListPrivate 이면
                            if (TABLE_id.equalsIgnoreCase("MainContent_CenterContent_ctlBoardListPrivate")) {

                                //TABLE태그의 id 값이 MainContent_CenterContent_ctlBoardListPrivate 인 Element에서 모든 TR태그를 가져옴
                                List<Element> listTR=TABLE.getAllElements(HTMLElementName.TR);

                                for(int j=0; j<listTR.size(); j++) {

                                    //모든 TR태크 중에서 j 번째 TR 태그를 가져옴
                                    Element TR = listTR.get(j);

                                    //j번째 TR태그에서 모든 TD태그를 가져옴
                                    List<Element> listTD=TR.getAllElements(HTMLElementName.TD);

                                    for(int l=0; l<listTD.size(); l++){

                                        // 모근 TD태그 중에서 l 번째 TD 태그를 가져옴
                                        Element TD = listTD.get(l);

                                        //num title name date view 순으로 되있으므로 l값에 따라 값을 저장
                                        switch (l){
                                            case 0:
                                                num=TD.getTextExtractor().toString();
                                                break;
                                            case 1:
                                                List<Element> listA=TD.getAllElements(HTMLElementName.A);
                                                Element A=listA.get(0);
                                                href=A.getAttributeValue("href");
                                                title=TD.getTextExtractor().toString();
                                                break;
                                            case 2:
                                                name=TD.getTextExtractor().toString();
                                                break;
                                            case 3:
                                                date=TD.getTextExtractor().toString();
                                                break;
                                            case 4:
                                                view=TD.getTextExtractor().toString();
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
                                            "                                        ","");
                                    name = name.replace("\n" +
                                            "                                    ","");
                                    //date 가공
                                    date=date.replace("\n" +
                                            "                                        ","");
                                    date=date.replace("\n" +
                                            "                                    ","");

                                    //아이템 추가
                                    adapter.addItem(num, title, name, date, view);

                                    listHref.add(href);
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.d("error :::: ", e.getMessage());
                }
            }
        };

        Thread thread = new Thread(runnable);
        try {
            thread.start();
            thread.join();
        } catch(Exception e) {

        }

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View i) {
                page_int=Integer.parseInt(page)+1;
                page= String.valueOf(page_int);
                pageView.setText(page);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Source source;
                        try {
                            //아이템 초기화
                            adapter.clear();
                            listHref.clear();

                            URL url = new URL(parsing_url+"&Page="+page);
                            source = new Source(url);

                            // date 가져오기

                            // 모든 TABLE 태그를 가져와 리스트에 담음
                            List<Element> listTABLE = source.getAllElements(HTMLElementName.TABLE);

                            for (int i = 0; i < listTABLE.size(); i++) {
                                Element TABLE = listTABLE.get(i);

                                // TABLE태그의 id 값을 가져옴
                                String TABLE_id = TABLE.getAttributeValue("id");
                                if (TABLE_id != null) {

                                    // id 값이 MainContent_CenterContent_ctlBoardListPrivate 이면
                                    if (TABLE_id.equalsIgnoreCase("MainContent_CenterContent_ctlBoardListPrivate")) {

                                        //TABLE태그의 id 값이 MainContent_CenterContent_ctlBoardListPrivate 인 Element에서 모든 TR태그를 가져옴
                                        List<Element> listTR=TABLE.getAllElements(HTMLElementName.TR);

                                        for(int j=0; j<listTR.size(); j++) {

                                            //모든 TR태크 중에서 j 번째 TR 태그를 가져옴
                                            Element TR = listTR.get(j);

                                            //j번째 TR태그에서 모든 TD태그를 가져옴
                                            List<Element> listTD=TR.getAllElements(HTMLElementName.TD);

                                            for(int l=0; l<listTD.size(); l++){

                                                // 모근 TD태그 중에서 l 번째 TD 태그를 가져옴
                                                Element TD = listTD.get(l);

                                                //num title name date view 순으로 되있으므로 l값에 따라 값을 저장
                                                switch (l){
                                                    case 0:
                                                        num=TD.getTextExtractor().toString();
                                                        break;
                                                    case 1:
                                                        List<Element> listA=TD.getAllElements(HTMLElementName.A);
                                                        Element A=listA.get(0);
                                                        href=A.getAttributeValue("href");
                                                        title=TD.getTextExtractor().toString();
                                                        break;
                                                    case 2:
                                                        name=TD.getTextExtractor().toString();
                                                        break;
                                                    case 3:
                                                        date=TD.getTextExtractor().toString();
                                                        break;
                                                    case 4:
                                                        view=TD.getTextExtractor().toString();
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
                                                    "                                        ","");
                                            name = name.replace("\n" +
                                                    "                                    ","");
                                            //date 가공
                                            date=date.replace("\n" +
                                                    "                                        ","");
                                            date=date.replace("\n" +
                                                    "                                    ","");

                                            //아이템 추가
                                            adapter.addItem(num, title, name, date, view);

                                            //listHref에 추가 링크 정보 추가
                                            listHref.add(href);

                                            //새로고침
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            Log.d("error :::: ", e.getMessage());
                        }
                    }
                };

                Thread thread = new Thread(runnable);
                try {
                    thread.start();
                    thread.join();
                } catch(Exception e) {

                }
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View i) {
                if(page_int>1) {
                    page_int = Integer.parseInt(page) - 1;
                    page = String.valueOf(page_int);
                    pageView.setText(page);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Source source;
                            try {
                                //아이템 초기화
                                adapter.clear();
                                //listHref.clear();

                                URL url = new URL(parsing_url+"&Page="+page);
                                source = new Source(url);

                                // date 가져오기

                                // 모든 TABLE 태그를 가져와 리스트에 담음
                                List<Element> listTABLE = source.getAllElements(HTMLElementName.TABLE);

                                for (int i = 0; i < listTABLE.size(); i++) {
                                    Element TABLE = listTABLE.get(i);

                                    // TABLE태그의 id 값을 가져옴
                                    String TABLE_id = TABLE.getAttributeValue("id");
                                    if (TABLE_id != null) {

                                        // id 값이 MainContent_CenterContent_ctlBoardListPrivate 이면
                                        if (TABLE_id.equalsIgnoreCase("MainContent_CenterContent_ctlBoardListPrivate")) {

                                            //TABLE태그의 id 값이 MainContent_CenterContent_ctlBoardListPrivate 인 Element에서 모든 TR태그를 가져옴
                                            List<Element> listTR=TABLE.getAllElements(HTMLElementName.TR);

                                            for(int j=0; j<listTR.size(); j++) {

                                                //모든 TR태크 중에서 j 번째 TR 태그를 가져옴
                                                Element TR = listTR.get(j);

                                                //j번째 TR태그에서 모든 TD태그를 가져옴
                                                List<Element> listTD=TR.getAllElements(HTMLElementName.TD);

                                                for(int l=0; l<listTD.size(); l++){

                                                    // 모근 TD태그 중에서 l 번째 TD 태그를 가져옴
                                                    Element TD = listTD.get(l);

                                                    //num title name date view 순으로 되있으므로 l값에 따라 값을 저장
                                                    switch (l){
                                                        case 0:
                                                            num=TD.getTextExtractor().toString();
                                                            break;
                                                        case 1:
                                                            List<Element> listA=TD.getAllElements(HTMLElementName.A);
                                                            Element A=listA.get(0);
                                                            href=A.getAttributeValue("href");
                                                            title=TD.getTextExtractor().toString();
                                                            break;
                                                        case 2:
                                                            name=TD.getTextExtractor().toString();
                                                            break;
                                                        case 3:
                                                            date=TD.getTextExtractor().toString();
                                                            break;
                                                        case 4:
                                                            view=TD.getTextExtractor().toString();
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
                                                        "                                        ","");
                                                name = name.replace("\n" +
                                                        "                                    ","");
                                                //date 가공
                                                date=date.replace("\n" +
                                                        "                                        ","");
                                                date=date.replace("\n" +
                                                        "                                    ","");

                                                //아이템 추가
                                                adapter.addItem(num, title, name, date, view);

                                                //새로고침
                                                adapter.notifyDataSetChanged();

                                                //listHref에 추가 링크 정보 추가
                                                listHref.add(href);
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.d("error :::: ", e.getMessage());
                            }
                        }
                    };

                    Thread thread = new Thread(runnable);
                    try {
                        thread.start();
                        thread.join();
                    } catch(Exception e) {

                    }
                }
            }
        });

        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    intent.putExtra("href", listHref.get(position));
                    startActivity(intent);
                }
                catch (Exception e){
                    Log.d("ERROR ::::",e.getMessage());
                }
            }
        });
    }

    /*private ListViewAdapter getItem(final String parsing_url, final ListViewAdapter adapter) {

        return adapter;
    }*/

}
