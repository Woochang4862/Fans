package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

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
    String num = "num", title = "title", name = "name", date = "date", view = "view", href = "href";
    ArrayList<String> listHref;
    //파싱할 URL
    String parsing_url;
    //String형 page 및 Integer형 page
    String page = "1";
    int page_int = 1;
    //TextView 객체=>page
    TextView pageView;
    //Refresh를 위한 객체
    PullRefreshLayout layout;
    //Page 이동 및 Home버튼
    ImageView back, forward, home;
    //ListView
    ListView menu;
    ArrayList<String> menu_list;
    ArrayAdapter menu_list_adapter;
    String board_name=null;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //내용을 한번 리스트뷰에 뿌려준다
        adapter = getItem(parsing_url, adapter);

        //pageView를 터치시 페이지검색창으로 이동
        pageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = (parsing_url.substring(parsing_url.indexOf("&Page=") + 6).equalsIgnoreCase("")) ? "1" : parsing_url.substring(parsing_url.indexOf("&Page=") + 6);
                Intent intent = new Intent(MainActivity.this, InputPageActivity.class);
                intent.putExtra("pageInfo", page);
                startActivityForResult(intent, 1);
            }
        });

        //board의 position번째 아이템이 터치되었을 때 listHref에서 position번째 요소를 intent로 넘겨줌
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    intent.putExtra("href", listHref.get(position));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("ERROR ::::", e.getMessage());
                }
            }
        });

        //리스트가 당겨졌을 때 리프레쉬되게 한다
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                //이게 없으면 무한 반복
                layout.setRefreshing(false);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                adapter = getItem(parsing_url, adapter);
            }
        });

        //back_btn이 눌렸을때
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page_int > 1) {
                    page = (parsing_url.substring(parsing_url.indexOf("&Page=") + 6).equalsIgnoreCase("")) ? "1" : parsing_url.substring(parsing_url.indexOf("&Page=") + 6);
                    page_int = Integer.parseInt(page) - 1;
                    page = String.valueOf(page_int);
                    pageView.setText(page);
                    parsing_url = parsing_url.substring(0, parsing_url.indexOf("&Page=") + 6) + page;
                    System.out.println(parsing_url);
                    adapter = getItem(parsing_url, adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //forward_btn이 눌렸을 때
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    page = (parsing_url.substring(parsing_url.indexOf("&Page=") + 6).equalsIgnoreCase("")) ? "1" : parsing_url.substring(parsing_url.indexOf("&Page=") + 6);
                    page_int = Integer.parseInt(page) + 1;
                    page = String.valueOf(page_int);
                    pageView.setText(page);
                    parsing_url = parsing_url.substring(0, parsing_url.indexOf("&Page=") + 6) + page;
                    System.out.println(parsing_url);
                    adapter = getItem(parsing_url, adapter);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Error:::", e.getMessage());
                }
            }
        });

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: //2PM
                        board_name="2PM_Notice";
                        break;
                    case 1: //GOT7
                        board_name="GOT7_Notice";
                        break;
                    case 2: //15&
                        board_name="15and_notice";
                        break;
                    case 3: //missA
                        board_name="missA_notice";
                        break;
                    case 4: //Ayeon
                        board_name="100ayeon_notice";
                        break;
                    case 5: //TWICE
                        board_name="twice_notice";
                        break;
                    case 6: //Day6
                        board_name="DAY6_Notice";
                        break;
                    case 7: //NakJoon
                        board_name="Notice_NakJoon";
                        break;
                    case 8: //J.Y.Park
                        board_name="NOTICE_JYP";
                        break;
                    case 9: //Wonder Girls
                        board_name="WonderGirls_Notice";
                        break;
                    case 10: //Suzy
                        board_name="NOTICE_SUZY";
                        break;
                    case 11: //Somi
                        board_name="Notice_Somi";
                        break;
                    case 12: //StarKids
                        board_name="NOTICE_SK";
                        break;
                }
                init();
                getItem(parsing_url, adapter);
                drawerLayout.closeDrawer(Gravity.LEFT);
                adapter.notifyDataSetChanged();
            }
        });

        //리스트가 Drag되었을때를 감지하여
//        board.setOnTouchListener(new View.OnTouchListener() {
//            public float pressedX;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                float distance = 0;
//
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // 손가락을 touch 했을 떄 x 좌표값 저장
//                        pressedX = event.getX();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // 손가락을 떼었을 때 저장해놓은 x좌표와의 거리 비교
//                        distance = pressedX - event.getX();
//                        break;
//                }
//
//                // 해당 거리가 100이 되지 않으면 이벤트 처리 하지 않는다.
//                if (Math.abs(distance) < 30) {
//                    return false;
//                }
//                if (distance > 0) {
//                    // 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
//
//                } else {
//                    // 손가락을 오른쪽으로 움직였으면 왼쪽 화면이 나타나야 한다.
//                }
//                return true;
//            }
//        });

    }

    //초기화
    private void init() {
        //url 지정
        parsing_url = "https://fans.jype.com/BoardList?BoardName="+board_name+"&SearchField=&SearchQuery=&Page=";

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        board = (ListView) findViewById(R.id.board);
        board.setAdapter(adapter);

        //pageView 지정
        pageView = (TextView) findViewById(R.id.page);

        //pageView를 page기본값(1)으로 지정
        pageView.setText(page);

        //추가 링크 정보를 담을 ArrayList 생성
        listHref = new ArrayList<String>();

        //Draw를 통한 Refresh를 하기 위한 객체 지정
        layout = (PullRefreshLayout) findViewById(R.id.swipe);

        //페이지이동을 위한 ImageView지정
        back = (ImageView) findViewById(R.id.back_btn);
        forward = (ImageView) findViewById(R.id.forward_btn);

        //기본페이지로 이동을 위한 ImageView지정
        home = (ImageView) findViewById(R.id.home_btn);

        //menu로 쓰일 ListView 객체 지정
        menu=(ListView)findViewById(R.id.drawer_menulist);

        //ListView에 들어갈 항목 추가 및 Adapter 생성
        menu_list=new ArrayList<String>();
        menu_list.add("2PM");
        menu_list.add("GOT7");
        menu_list.add("15&");
        menu_list.add("missA");
        menu_list.add("Ayeon");
        menu_list.add("TWICE");
        menu_list.add("DAY6");
        menu_list.add("NakJoon");
        menu_list.add("J.Y.Park");
        menu_list.add("Wonder Girls");
        menu_list.add("Suzy");
        menu_list.add("Somi");
        menu_list.add("StarKids");
        menu_list_adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,menu_list);

        //Adapter지정
        menu.setAdapter(menu_list_adapter);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

    }

    //requestCode가 1이고 resultCode가 RESULT_OK intent로 시작되었을떄는 아래의 내용을 실행한다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("pageInfo");
                try {
                    if (Integer.parseInt(result) > 0) {
                        page = result;
                        pageView.setText(page);
                        parsing_url = parsing_url.substring(0, parsing_url.indexOf("&Page=") + 6) + page;
                        adapter = getItem(parsing_url, adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "올바른 숫자를 입력해 주시기 바랍니다", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "올바른 숫자를 입력해 주시기 바랍니다", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //게시판 파싱
    private ListViewAdapter getItem(final String parsing_url, final ListViewAdapter adapter) {
        adapter.clear();
        listHref.clear();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Source source;
                try {
                    URL url = new URL(parsing_url);
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
        } catch (Exception e) {

        }
        return adapter;
    }

    //OptionMenu를 만든다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_search, menu);

        //SearchView 객체를 생성
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //Spinner객체 생성
        final Spinner search_type_spinner = (Spinner) menu.findItem(R.id.action_spinner).getActionView();
        //Spinner 내용
        ArrayList<String> search_type;
        //Spinner Adapter
        ArrayAdapter<String> type_adapter;
        //검색 유형 및 Adapter 지정
        search_type = new ArrayList<String>();
        search_type.add("제목");
        search_type.add("내용");
        search_type.add("닉네임");
        type_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, search_type);
        search_type_spinner.setPrompt("유형");
        search_type_spinner.setAdapter(type_adapter);

        //searchView의 Query기 submit, change 됬을 떄 Override Method의 내용을 각각 실행한다
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String search_field = null;
                switch (search_type_spinner.getSelectedItem().toString()) {
                    case "제목":
                        search_field = "Title";
                        break;
                    case "내용":
                        search_field = "Content";
                        break;
                    case "닉네임":
                        search_field = "Name";
                }
                parsing_url = parsing_url.substring(0, parsing_url.indexOf("&SearchField=") + 13) + search_field + "&SearchQuery=" + query + "&Page=" + "1";
                page = "1";
                pageView.setText(page);
                adapter = getItem(parsing_url, adapter);
                adapter.notifyDataSetChanged();
                search_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String search_field = null;
                        switch (search_type_spinner.getSelectedItem().toString()) {
                            case "제목":
                                search_field = "Title";
                                break;
                            case "내용":
                                search_field = "Content";
                                break;
                            case "닉네임":
                                search_field = "Name";
                        }
                        parsing_url = parsing_url.substring(0, parsing_url.indexOf("&SearchField=") + 13) + search_field + "&SearchQuery=" + query + "&Page=" + "1";
                        page = "1";
                        pageView.setText(page);
                        adapter = getItem(parsing_url, adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(MainActivity.this, "올바른 유형을 선택해주세요", Toast.LENGTH_SHORT);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                String search_field = null;
                switch (search_type_spinner.getSelectedItem().toString()) {
                    case "제목":
                        search_field = "Title";
                        break;
                    case "내용":
                        search_field = "Content";
                        break;
                    case "닉네임":
                        search_field = "Name";
                }
                parsing_url = parsing_url.substring(0, parsing_url.indexOf("&SearchField=") + 13) + search_field + "&SearchQuery=" + newText + "&Page=" + "1";
                page = "1";
                pageView.setText(page);
                adapter = getItem(parsing_url, adapter);
                adapter.notifyDataSetChanged();
                search_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String search_field = null;
                        switch (search_type_spinner.getSelectedItem().toString()) {
                            case "제목":
                                search_field = "Title";
                                break;
                            case "내용":
                                search_field = "Content";
                                break;
                            case "닉네임":
                                search_field = "Name";
                        }
                        parsing_url = parsing_url.substring(0, parsing_url.indexOf("&SearchField=") + 13) + search_field + "&SearchQuery=" + newText + "&Page=" + "1";
                        page = "1";
                        pageView.setText(page);
                        adapter = getItem(parsing_url, adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(MainActivity.this, "올바른 유형을 선택해주세요", Toast.LENGTH_SHORT);
                    }
                });
                return false;
            }

        });
        return true;
    }
}
