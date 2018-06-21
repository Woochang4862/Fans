package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.jeong_woochang.fans.Adapter.DrawerAdapter;
import com.example.jeong_woochang.fans.Adapter.ListViewAdapter;
import com.example.jeong_woochang.fans.POJO.DrawerItem;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

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
    //Refresh를 위한 객체
    PullRefreshLayout layout;
    //Home버튼
    ImageView home;
    //ListView
    ListView menu;
    DrawerAdapter menu_list_adapter;
    String board_name=null;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ProgressBar progressBar;
    private boolean lastItemVisibleFlag=false;
    private boolean mLockListView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //내용을 한번 리스트뷰에 뿌려준다
        adapter = getItem(parsing_url, adapter);

        board.setOnScrollListener(this);

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
                parsing_url="https://fans.jype.com/BoardList?BoardName=&SearchField=&SearchQuery=&Page=";
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        });

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: //2PM
                        board_name="2PM_Notice";
                        setTitle("2PM");
                        break;
                    case 1: //GOT7
                        board_name="GOT7_Notice";
                        setTitle("GOT7");
                        break;
                    case 2: //15&
                        board_name="15and_notice";
                        setTitle("15&");
                        break;
                    case 3: //missA
                        board_name="missA_notice";
                        setTitle("MissA");
                        break;
                    case 4: //Ayeon
                        board_name="100ayeon_notice";
                        setTitle("Ayeon");
                        break;
                    case 5: //TWICE
                        board_name="twice_notice";
                        setTitle("TWICE");
                        break;
                    case 6: //Day6
                        board_name="DAY6_Notice";
                        setTitle("Day6");
                        break;
                    case 7: //NakJoon
                        board_name="Notice_NakJoon";
                        setTitle("NakJoon");
                        break;
                    case 8: //J.Y.Park
                        board_name="NOTICE_JYP";
                        setTitle("J.Y.Park");
                        break;
                    case 9: //Wonder Girls
                        board_name="WonderGirls_Notice";
                        setTitle("Wonder Girls");
                        break;
                    case 10: //Suzy
                        board_name="NOTICE_SUZY";
                        setTitle("Suzy");
                        break;
                    case 11: //Somi
                        board_name="Notice_Somi";
                        setTitle("Somi");
                        break;
                    case 12: //StarKids
                        board_name="NOTICE_SK";
                        setTitle("StarKids");
                        break;
                }
                init();
                getItem(parsing_url, adapter);
                drawerLayout.closeDrawer(Gravity.LEFT);
                adapter.notifyDataSetChanged();
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
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

        //추가 링크 정보를 담을 ArrayList 생성
        listHref = new ArrayList<String>();

        //Draw를 통한 Refresh를 하기 위한 객체 지정
        layout = (PullRefreshLayout) findViewById(R.id.swipe);

        //기본페이지로 이동을 위한 ImageView지정
        home = (ImageView) findViewById(R.id.home_btn);

        //menu로 쓰일 ListView 객체 지정
        menu=(ListView)findViewById(R.id.drawer_menulist);

        //ProgressBar
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        //ListView에 들어갈 항목 추가 및 Adapter 생성
        menu_list_adapter=new DrawerAdapter();
        new GetFanList().execute();

        //Adapter지정
        menu.setAdapter(menu_list_adapter);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        setTitle("");
        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
    }

    //게시판 파싱
    private ListViewAdapter getItem(final String parsing_url, final ListViewAdapter adapter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Source source;
                try {
                    URL url = new URL(parsing_url);
                    source = new Source(url);

                    // date 가져오기

                    //모든 TABLE 태그를 가져와 리스트에 담음
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    mLockListView = false;
                }
            },1000);
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            progressBar.setVisibility(View.VISIBLE);

            page = (parsing_url.substring(parsing_url.indexOf("&Page=") + 6).equalsIgnoreCase("")) ? "1" : parsing_url.substring(parsing_url.indexOf("&Page=") + 6);
            page_int = Integer.parseInt(page) + 1;
            page = String.valueOf(page_int);
            parsing_url = parsing_url.substring(0, parsing_url.indexOf("&Page=") + 6) + page;
            System.out.println(parsing_url);

            // 다음 데이터를 불러온다.
            adapter=getItem(parsing_url,adapter);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    public class GetFanList extends AsyncTask<Void,Void,DrawerAdapter> {


        @Override
        protected DrawerAdapter doInBackground(Void... voids) {
            ArrayList<DrawerItem> list=new ArrayList<DrawerItem>();

            try {
                // 로그인 페이지 접속
                Connection.Response loginPageResponse = Jsoup.connect("https://fans.jype.com/Default")
                        .timeout(3000)
                        .header("Origin", "https://fans.jype.com/")
                        .header("Referer", "https://fans.jype.com/Default")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                        .method(Connection.Method.GET)
                        .execute();
                Map<String, String> loginTryCookie = loginPageResponse.cookies();
                Document loginPageDocument = loginPageResponse.parse();

                String ofp = loginPageDocument.select("input.ofp").val();
                String nfp = loginPageDocument.select("input.nfp").val();
                Connection.Response response = Jsoup.connect("https://fans.jype.com/MyFans")
                        .method(Connection.Method.GET)
                        .execute();
                Document source= response.parse();
                System.out.println(source.html());
                Elements elements=source.select(".col-lg-6 #fanslist");
                for(org.jsoup.nodes.Element element: elements){
                    DrawerItem temp=new DrawerItem();
                    System.out.println(element);
                    org.jsoup.nodes.Element element1=element.select("img[class=img-responsive center-block]").first();
                    temp.setImg(element1.attr("src"));
                    element1=element.select("img[class=img-responsive center-block]").first();
                    temp.setName(element1.attr("src"));

                    System.out.println(temp);

                    menu_list_adapter.addItem(temp);
                }
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
        protected void onPostExecute(DrawerAdapter drawerAdapter) {
            super.onPostExecute(drawerAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
