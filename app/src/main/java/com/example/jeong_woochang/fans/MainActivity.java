package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
        setMenuInBackground();


        //menu로 쓰일 ListView 객체 지정
        menu = findViewById(R.id.drawer_menulist);
        menu.setAdapter(menu_list_adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                board_name = menu_list_adapter.getBoardName(position);
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (checkNotiStatus(board_name))
                    fabAddNoti.setImageResource(R.drawable.ic_remove);
                else
                    fabAddNoti.setImageResource(R.drawable.ic_add_alert);
                setBoardInBackground();
            }
        });


        //Pulling을 통해 Refresh를 하기 위한 객체 지정
        layout = findViewById(R.id.swipe);
        //리스트가 당겨졌을 때 리프레쉬되게 한다
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setBoardInBackground();
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

        board_name = load();
        board = findViewById(R.id.board);
        if(board_name!=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    adapter = new BoardRecyclerViewAdapter(gb.getItem(MainActivity.this, board_name, page, search_query, search_field));
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // RecyclerView 참조 및 Adapter달기
                            board.setHasFixedSize(false);
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
                        }
                    });
                }
            }).start();
        }
        else {
            //TODO:Tutorial Process
            adapter = new BoardRecyclerViewAdapter();
            final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            board.setHasFixedSize(false);
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
        }

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
                if(board_name==null){
                    Snackbar.make(v, "시작 화면 등록 중 오류가 발생했습니다. 앱을 재실행 주세요.", Snackbar.LENGTH_LONG)
                            .setAction("재실행", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                    return;
                }
                else {
                    save(board_name);
                    Toast.makeText(MainActivity.this, "앞으로 시작화면에 바로 표시할게요", Toast.LENGTH_SHORT).show();
                    fam.close(true);
                }
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
        search_type = new ArrayList<>();
        search_type.add("제목");
        search_type.add("내용");
        search_type.add("닉네임");
        type_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, search_type);
        search_type_spinner.setPrompt("유형");
        search_type_spinner.setAdapter(type_adapter);

        //searchView의 Query기 submit, change 됬을 떄 Override Method의 내용을 각각 실행한다
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String search_field = null;
                search_query=query;
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
                setSearch_field(search_field);
                setBoardInBackground();
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
                        setSearch_field(search_field);
                        setBoardInBackground();
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
    private void save(final String setting_board_name) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        editor.putString("board_name", setting_board_name);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private String load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        Object tmp;
        try {
            tmp = appData.getString("board_name", null);
        }catch (Exception e){
            appData.edit().clear().commit();
            e.printStackTrace();
            return null;
        }
        return (String) tmp;
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

    private void setMenuInBackground(){
        menu_list_adapter.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    menu_list_adapter.setArrayList(new GetFanList().execute().get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menu_list_adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void setBoardInBackground(){
        adapter.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter.setmList(gb.getItem(MainActivity.this,board_name,page,search_query,search_field));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void setSearch_field(String search_field) {
        this.search_field = search_field;
    }
}
