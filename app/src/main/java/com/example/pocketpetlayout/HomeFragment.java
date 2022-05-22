package com.example.pocketpetlayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private final String TAG = "MainFragment";


    private Toolbar toolbar; //툴바

    //DB
    DBHelper dbHelper;
    MyDbHelper myDbHelper;

    private ImageView userImage;
    private ImageView petImage;
    private TextView userText;
    private TextView petText;
    private AppCompatActivity activity;

    private FragmentManager fragmentManager;
    private FeedFragment feedFragment;

    private RecyclerView mQnaView;
    private ArrayList<QnAItem> mQnAList;
    private QnAAdapter mQnaAdapter;

    private RecyclerView mFeedView;
    private ArrayList<FeedItem> mFeedList;
    private HomeFeedAdapter mFeedAdapter;

    private Button qnaMoreBtn;
    private Button feedMoreBtn;

    //하단 버튼 없애기
    private View decorView;
    private int	uiOption;
    String username;
    String petname;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, ": onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fragmentManager = getFragmentManager();
        feedFragment = new FeedFragment();

        //툴바만들기
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        //----------

        //프로필 이미지 둥글게
        userImage = view.findViewById(R.id.UserImage);
        userImage.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21){
            userImage.setClipToOutline(true); }
        //----------

        //펫 이미지 둥글게
        petImage = view.findViewById(R.id.PetImage);
        petImage.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21){
            petImage.setClipToOutline(true); }
        //----------

        //사용자 텍스트
        username = "김유저";
        userText = view.findViewById(R.id.UserText);
        userText.setText("안녕하세요," + username + "님! \n" +
                "반려동물과 잘 지내고 계신가요?");
        //-----------

        //펫 텍스트
        petname = "토리";
        petText = view.findViewById(R.id.PetText);
        petText.setText("안녕, " + petname + "! \n" +
                "아픈 곳은 없니?");
        //-----------

        //더보기 버튼
        qnaMoreBtn = view.findViewById(R.id.MoreButton1);
        qnaMoreBtn.setOnClickListener(this::onClick);
        feedMoreBtn = view.findViewById(R.id.MoreButton2);
        feedMoreBtn.setOnClickListener(this::onClick);
        //-------------------

        //DBHelper
        dbHelper = new DBHelper(this.getActivity().getApplicationContext());
        myDbHelper = new MyDbHelper(this.getActivity().getApplicationContext());
        // QnA의 RecyclerView
        firstQnAInit(view);

        mQnaAdapter = new QnAAdapter(mQnAList);
        mQnaView.setAdapter(mQnaAdapter);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQnaView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        //--------------------


        //Feed의 RecyclerView
        firstFeedInit(view);

        mFeedAdapter = new HomeFeedAdapter(mFeedList);
        mFeedView.setAdapter(mFeedAdapter);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        //----------------

        //하단 버튼을 없애는 기능
        decorView = getActivity().getWindow().getDecorView();
        uiOption = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility( uiOption );
        //---------------------

        return view;
    }

    //ToolBar에 toolbar_menu.xml 을 인플레이트

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    //---------------

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item1:
                Toast.makeText(getActivity().getApplicationContext(), "무슨아이템 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;

            case R.id.menu_item2:
                Toast.makeText(getActivity().getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            default:
                return false;
        }
    }
    //--------------


    // 버튼 클릭 이벤트 리스너
    public void onClick(View view) {
        switch (view.getId()){
            //QnA 더보기
            case R.id.MoreButton1:
                Intent intent1 = new Intent(getActivity().getApplicationContext(), QnABoardActivity.class);
                startActivity(intent1);
                break;
            //피드 더보기
            case R.id.MoreButton2:
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, feedFragment).commit();
                break;
        }
    }
    //-------------------

    //QnA 관련 함수
    public void firstQnAInit(View view){

        mQnaView = (RecyclerView) view.findViewById(R.id.QnaView);
        mQnAList = new ArrayList<QnAItem>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                                    Board.COLUMN_TITLE + " ," +
                                    Board.COLUMN_IMAGE + " FROM " + Board.TABLE_NAME +
                                    " WHERE " +  Board.COLUMN_CATEGORY + " = 'QnA' " + ";", null );

        if (c.moveToFirst()) {
            do{
                String title = c.getString(0);
                String imgName = c.getString(1);

                mQnAList.add(new QnAItem(imgName, title));
                Log.i(TAG, "READ title :" + title + "img : " + imgName);
            }while (c.moveToNext());
        }
        c.close();
        db.close();

    }

    //------------


    //Feed 관련 함수
    public void firstFeedInit(View view){
        mFeedView = (RecyclerView) view.findViewById(R.id.FeedView);
        mFeedList = new ArrayList<>();

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + Feed.IMAGE + " FROM " + Feed.TABLE_NAME, null );

        if (c.moveToFirst()) {
            do{
                String imgName = c.getString(0);

                mFeedList.add(new FeedItem(imgName));
                Log.i(TAG, "READ img : " + imgName);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
    }
    //-----------------


}