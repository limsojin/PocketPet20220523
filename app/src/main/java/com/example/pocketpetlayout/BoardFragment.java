package com.example.pocketpetlayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class BoardFragment<HomeFragmentBinding> extends Fragment {
    private final String TAG = "BoardFragment";

    private ListItemsAdapter mListItemsAdapter;

    private TextView myBoard;
    private TextView bestBoard;
    private TextView freeBoard;
    private TextView qnaBoard;
    private TextView adoptBoard;
    private ArrayList<BoardItem> mItems;

    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, ": onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        // 버튼 이벤트
        myBoard = view.findViewById(R.id.MyBoard);
        bestBoard = view.findViewById(R.id.BestBoard);
        freeBoard = view.findViewById(R.id.FreeBoard);
        qnaBoard = view.findViewById(R.id.QnABoard);
        adoptBoard = view.findViewById(R.id.AdoptBoard);

        myBoard.setOnClickListener(this::onClick);
        bestBoard.setOnClickListener(this::onClick);
        freeBoard.setOnClickListener(this::onClick);
        qnaBoard.setOnClickListener(this::onClick);
        adoptBoard.setOnClickListener(this::onClick);

        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // 리스트 아이템
        firstInit(view);    // 객체 초기화 및 생성

        if(!mItems.isEmpty()) {

            //Listview 지정
            ListView recentListView = view.findViewById(R.id.list_items);

            // ListView Adpater 지정
            final BoardAdapter boardAdapter = new BoardAdapter(getActivity().getApplicationContext() ,mItems);

            // ListView의 어뎁터를 셋한다.
            recentListView.setAdapter(boardAdapter);

            // 아이템을 클릭 했을 때 동작하는 클릭 리스너
            recentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int Id = boardAdapter.getItem(position).getId();

                            Toast.makeText(getActivity().getApplicationContext(),
                            "선택한 board의 boardID :" + Id, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getActivity().getApplicationContext(), BoardContentsActivity.class);
                    intent.putExtra("BoardId", Id); //게시글 아이디를 전송
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    // 버튼 클릭 이벤트 리스너
    public void onClick(View view) {
        switch (view.getId()){
            //내가 쓴 글 버튼
            case R.id.MyBoard:
                Intent intent1 = new Intent(getActivity().getApplicationContext(), MyBoardActivity.class);
                startActivity(intent1);
                break;
            //베스트 글 버튼
            case R.id.BestBoard:
                Intent intent2 = new Intent(getActivity().getApplicationContext(), BestBoardActivity.class);
                startActivity(intent2);
                break;
            // 자유 게시판 버튼
            case R.id.FreeBoard:
                Intent intent3 = new Intent(getActivity().getApplicationContext(), FreeBoardActivity.class);
                startActivity(intent3);
                break;
            // QnA 게시판 버튼
            case R.id.QnABoard:
                Intent intent4 = new Intent(getActivity().getApplicationContext(), QnABoardActivity.class);
                startActivity(intent4);
                break;
            //입양 게시판 버튼
            case R.id.AdoptBoard:
                Intent intent5 = new Intent(getActivity().getApplicationContext(), AdoptBoardActivity.class);
                startActivity(intent5);
                break;
        }
    }

    public void firstInit(View v){
        mItems = new ArrayList<BoardItem>();

        Log.i(TAG, "InitializeQnABoardData!!!");

        // board 테이블에서  qna 카테고리인 튜플의 id, title, writer, reg_date, like_cnt, com_cnt 를 가져온다.

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                Board.COLUMN_BOARD_ID + " ," +
                Board.COLUMN_TITLE + " ," +
                Board.COLUMN_WRITER + " ," +
                Board.COLUMN_REGISTER_DATE + " ," +
                Board.COLUMN_LIKE_CNT + " ," +
                Board.COLUMN_COMMENT_CNT + " FROM " +
                Board.TABLE_NAME + ";", null);


        if (c.moveToFirst()) {

            do {
                int id = c.getInt(0);
                String title = c.getString(1);
                String writer = c.getString(2);
                String regDate = c.getString(3);
                int heart = c.getInt(4);
                int com = c.getInt(5);
                mItems.add(new BoardItem(id, title, writer, regDate, heart, com));
                Log.i(TAG, "READ id :" + id + "title :" + title + "writer :" + writer + "regDate" + regDate + "heart: " + heart + "com : " + com);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }
}