package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.jeong_woochang.fans.Adapter.BoardRecyclerViewAdapter;
import com.example.jeong_woochang.fans.Adapter.DrawerAdapter;
import com.example.jeong_woochang.fans.Method.GetBoard;
import com.example.jeong_woochang.fans.Method.GetFanList;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    //ListView 객체
    ListView menu;
    //RecyclerView 객체
    RecyclerView board;
    //Adapter
    BoardRecyclerViewAdapter adapter;
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
    private String board_name = null, page = "1", search_query="", search_field="";
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
    }

    //초기화
    private void init(){

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


        //menu로 쓰일 ListView 객체 지정
        menu = findViewById(R.id.drawer_menulist);
        menu.setAdapter(menu_list_adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                board_name = menu_list_adapter.arrayList.get(position).getName();
                adapter.clear();
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (checkNotiStatus(board_name))
                    fabAddNoti.setImageResource(R.drawable.ic_remove);
                else
                    fabAddNoti.setImageResource(R.drawable.ic_add_alert);
                adapter.addData(gb.getItem(MainActivity.this, board_name, page,search_query,search_field));
            }
        });


        //Pulling을 통해 Refresh를 하기 위한 객체 지정
        layout = findViewById(R.id.swipe);
        //리스트가 당겨졌을 때 리프레쉬되게 한다
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.addData(gb.getItem(MainActivity.this, board_name, page, search_query, search_field));
                adapter.notifyDataSetChanged();
                //이게 없으면 무한 반복
                layout.setRefreshing(false);
            }
        });


        //ProgressBar
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);


        //toolbar *start{
        toolbar = findViewById(R.id.toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));


        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        appData=getSharedPreferences("appData", MODE_PRIVATE);

        getBoard_name(load());
        adapter = new BoardRecyclerViewAdapter(gb.getItem(MainActivity.this, board_name, page,search_query,search_field));
        // RecyclerView 참조 및 Adapter달기
        board = findViewById(R.id.board);
        board.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        board.setLayoutManager(layoutManager);
        adapter.SetOnItemClickListener(new BoardRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("href", adapter.getHref(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "long click " + position, Toast.LENGTH_SHORT).show();
            }
        });
        board.setAdapter(adapter);

        fabStar = findViewById(R.id.star);
        fabAddNoti = findViewById(R.id.noti);
        if (checkNotiStatus(board_name)) {
            fabAddNoti.setImageResource(R.drawable.ic_remove);
        }
        else {
            fabAddNoti.setImageResource(R.drawable.ic_add_alert);
        }
        fam= findViewById(R.id.fam);
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

    //Menu
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
                adapter.addData(gb.getItem(MainActivity.this,board_name,page,query,search_field));
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
                        adapter.addData(gb.getItem(MainActivity.this,board_name,page,query,search_field));
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
        int result;
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
