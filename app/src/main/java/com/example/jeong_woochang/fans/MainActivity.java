package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.jeong_woochang.fans.Adapter.DrawerAdapter;
import com.example.jeong_woochang.fans.Adapter.ListViewAdapter;
import com.example.jeong_woochang.fans.Method.GetBoard;
import com.example.jeong_woochang.fans.Method.GetFanList;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    //ListView 객체
    ListView board, menu;
    //list와 listview를 연결할 Adapter
    ListViewAdapter adapter;
    DrawerAdapter menu_list_adapter;
    ProgressBar progressBar;
    private boolean lastItemVisibleFlag = false;
    public boolean mLockListView = false;
    //Refresh를 위한 객체
    PullRefreshLayout layout;
    //Toolbar
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    //URL 파라미터
    String board_name = null;
    String page = "1";
    private String search_query="";
    private String search_field="";
    //Method Class
    GetBoard gb=new GetBoard();
    SharedPreferences appData;
    FloatingActionButton fabStar, fabAddNoti;
    FloatingActionMenu fam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //초기화
        init();

        board.setOnScrollListener(this);

        //board의 position번째 아이템이 터치되었을 때 listHref에서 position번째 요소를 intent로 넘겨줌
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Log.d("######",String.valueOf(position));
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    intent.putExtra("href", adapter.listViewItems.get(position).getHref());
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

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getBoard_name(position);
                adapter.clear();
                adapter=gb.getItem(MainActivity.this, adapter, board_name, page,search_query,search_field);
                drawerLayout.closeDrawer(Gravity.LEFT);
                adapter.notifyDataSetChanged();
                if (checkNotiStatus(board_name))
                    fabAddNoti.setImageResource(R.drawable.ic_remove);
                else
                    fabAddNoti.setImageResource(R.drawable.ic_add_alert);
            }
        });

        fabStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(getBoardNumber(board_name));
                Toast.makeText(MainActivity.this, "앞으로 시작화면에 바로 표시할게요", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });

        fabAddNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNotiStatus(board_name)) {
                    notiOff(board_name);
                    Toast.makeText(MainActivity.this, "새 글이 올라와도 안 알려줄게요", Toast.LENGTH_SHORT).show();
                    fabAddNoti.setImageResource(R.drawable.ic_add_alert);
                } else {
                    notiOn(board_name);
                    Toast.makeText(MainActivity.this, "새 글이 올라오면 알려줄게요", Toast.LENGTH_SHORT).show();
                    fabAddNoti.setImageResource(R.drawable.ic_remove);
                }

            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
    }

    //초기화
    private void init(){

        // Adapter 생성
        adapter = new ListViewAdapter();

        //ListView에 들어갈 항목 추가 및 Adapter 생성
        menu_list_adapter = new DrawerAdapter();
        menu_list_adapter.clear();
        try {
            menu_list_adapter=new GetFanList().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //menu_list_adapter.addItem("https://jypfanscdn.azureedge.net/portal/dance-the-night-away_fan's_398x285.jpg","https://jypfanscdn.azureedge.net/portal/TW_fans-title(1).jpg");
        menu_list_adapter.notifyDataSetChanged();

        // 리스트뷰 참조 및 Adapter달기
        board = (ListView) findViewById(R.id.board);
        board.setAdapter(adapter);

        //menu로 쓰일 ListView 객체 지정
        menu = (ListView) findViewById(R.id.drawer_menulist);
        menu.setAdapter(menu_list_adapter);

        //Pulling을 통해 Refresh를 하기 위한 객체 지정
        layout = (PullRefreshLayout) findViewById(R.id.swipe);

        //ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        //toolbar *start{
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        setTitle("");
        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        appData=getSharedPreferences("appData", MODE_PRIVATE);
        getBoard_name(load());
        if(!(board_name.equalsIgnoreCase("")||board_name==null))
            adapter=gb.getItem(MainActivity.this, adapter, board_name, page,search_query,search_field);

        fabStar=(FloatingActionButton)findViewById(R.id.star);
        fabAddNoti=(FloatingActionButton)findViewById(R.id.noti);
        if (checkNotiStatus(board_name))
            fabAddNoti.setImageResource(R.drawable.ic_remove);
        else
            fabAddNoti.setImageResource(R.drawable.ic_add_alert);
        fam=(FloatingActionMenu)findViewById(R.id.fam);
        //}end*
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
                page = "1";
                adapter.clear();
                adapter = gb.getItem(MainActivity.this, adapter,board_name,page,query,search_field);
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
                        page = "1";
                        adapter.clear();
                        adapter = gb.getItem(MainActivity.this, adapter,board_name,page,query,search_field);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(MainActivity.this, "올바른 유형을 선택해주세요", Toast.LENGTH_SHORT);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
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

            board.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

            page = String.valueOf(Integer.parseInt(page)+1);

            // 다음 데이터를 불러온다.
            adapter = gb.getItem(MainActivity.this, adapter,board_name,page,search_query,search_field);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    public void setmLockListView(boolean mLockListView) {
        this.mLockListView = mLockListView;
    }

    // 설정값을 저장하는 함수
    private void save(final int setting_board_number) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        editor.putInt("board_name", setting_board_number);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private int load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        return  appData.getInt("board_name", -1);
    }

    private void getBoard_name(int n) {
        switch (n) {
            case 0: //2PM
                board_name = "2PM_Notice";
                setTitle("2PM");
                break;
            case 1: //GOT7
                board_name = "GOT7_Notice";
                setTitle("GOT7");
                break;
            case 2: //15&
                board_name = "15and_notice";
                setTitle("15&");
                break;
            case 3: //missA
                board_name = "missA_notice";
                setTitle("MissA");
                break;
            case 4: //Ayeon
                board_name = "100ayeon_notice";
                setTitle("Ayeon");
                break;
            case 5: //TWICE
                board_name = "twice_notice";
                setTitle("TWICE");
                break;
            case 6: //Day6
                board_name = "DAY6_Notice";
                setTitle("Day6");
                break;
            case 7: //NakJoon
                board_name = "Notice_NakJoon";
                setTitle("NakJoon");
                break;
            case 8: //J.Y.Park
                board_name = "NOTICE_JYP";
                setTitle("J.Y.Park");
                break;
            case 9: //Wonder Girls
                board_name = "WonderGirls_Notice";
                setTitle("Wonder Girls");
                break;
            case 10: //Suzy
                board_name = "NOTICE_SUZY";
                setTitle("Suzy");
                break;
            case 11: //Somi
                board_name = "Notice_Somi";
                setTitle("Somi");
                break;
            case 12: //StarKids
                board_name = "NOTICE_SK";
                setTitle("StarKids");
                break;
            default:
                board_name="";
        }
    }

    private int getBoardNumber(final String board_name){
        int result=0;
        switch (board_name){
            case "2PM_Notice":
                result=0;
                break;
            case "GOT7_Notice":
                result=1;
                break;
            case "15and_notice":
                result=2;
                break;
            case "missA_notice":
                result=3;
                break;
            case "100ayeon_notice":
                result=4;
                break;
            case "twice_notice":
                result=5;
                break;
            case "DAY6_Notice":
                result=6;
                break;
            case "Notice_NakJoon":
                result=7;
                break;
            case "NOTICE_JYP":
                result=8;
                break;
            case "WonderGirls_Notice":
                result=9;
                break;
            case "NOTICE_SUZY":
                result=10;
                break;
            case "Notice_Somi":
                result=11;
                break;
            case "NOTICE_SK":
                result=12;
                break;
            default:
                result=-1;
        }
        return result;
    }

    private void notiOn(String setting_board_name) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        editor.putBoolean(setting_board_name, true);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    private void notiOff(String setting_board_name) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        editor.putBoolean(setting_board_name, false);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    private boolean checkNotiStatus(final String board_name){
        return appData.getBoolean(board_name, false);
    }
}
