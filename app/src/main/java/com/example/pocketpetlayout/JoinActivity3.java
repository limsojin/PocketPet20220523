package com.example.pocketpetlayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;

public class JoinActivity3 extends AppCompatActivity {

    String nickSt;
    String gender;
    SQLiteDatabase database;
    ImageView pro;
    private static final int REQUEST_CODE = 0;
    Bitmap bitmap;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.join_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join3);

        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext());
        database = myDbHelper.getWritableDatabase();

        Button nextbtn2 = (Button) findViewById(R.id.nextbtn2); // 다음페이지로 넘기는 버튼
        EditText nickname = (EditText) findViewById(R.id.nickname); // 닉네임
        Spinner spinner_year = (Spinner)findViewById(R.id.spinner_year); // 월 선택
        Spinner spinner_month = (Spinner)findViewById(R.id.spinner_month); // 년 선택
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup); // 성별 선택
        pro = findViewById(R.id.pro);

        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu pop = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.join_menu, pop.getMenu());
                int permissonCheck = ContextCompat.checkSelfPermission(JoinActivity3.this, Manifest.permission.CAMERA);

                if(permissonCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(JoinActivity3.this, new String[]{Manifest.permission.CAMERA},0 );
                }else{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,1);
                }
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_camera: //  카메라를 이용하여 프로필 변경
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                activityResultPicture.launch(intent);
                                break;
                            case R.id.menu_gallery: // 갤러리를 이용하여 프로필 변경
                                Intent intent2 = new Intent();
                                intent2.setType("image/*");
                                intent2.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent2, REQUEST_CODE);
                                break;
                        }
                        return true;
                    }
                });
                pop.show();
            }
        });

        nextbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), JoinActivity4.class);
                String spinnerYear = spinner_year.getSelectedItem().toString(); // 선택한 년도 spinnerYear에 넣음
                String spinnerMonth = spinner_month.getSelectedItem().toString(); // 선택한 월 spinnerMonth에 넣음

                int birthDay = Integer.parseInt(spinnerYear + spinnerMonth);
                nickSt= nickname.getText().toString(); // 닉네임 넣음

                if(nickSt.length() == 0){
                    Toast toast = Toast.makeText(JoinActivity3.this, "닉네임은 필수입력 사항입니다",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId(); // 선택한 성별 넣음
                if (checkedRadioButtonId == -1) {
                    // No item selected
                }

                else{
                    if (checkedRadioButtonId == R.id.woman) {
                        gender= "woman";
                    }else if(checkedRadioButtonId == R.id.man){
                        gender= "man";
                    }
                }

                intent.putExtra("NICKNAME",nickSt); // 보내고
                intent.putExtra("BIRTHDAY",birthDay);
                intent.putExtra("SEX", gender);

                String emailSt = getIntent().getStringExtra("MEMBER_ID"); //받고
                String passwordSt2 =getIntent().getStringExtra("PASSWORD");

                intent.putExtra("MEMBER_ID",emailSt); // 2에서 받은 걸 다시 보내고
                intent.putExtra("PASSWORD", passwordSt2);

                startActivity(intent);

            }
        });
    }

    // 카메라 사진
    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        Bundle extras = result.getData().getExtras();

                        bitmap = (Bitmap) extras.get("data");
                        RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                        bitmapDrawable.setCircular(true);
                        pro.setImageDrawable(bitmapDrawable);
                    }
                }
            });

    @Override // 갤러리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    pro.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}