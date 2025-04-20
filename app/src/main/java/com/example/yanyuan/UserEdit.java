package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class UserEdit extends AppCompatActivity implements UserIfm.OnButtonClickListener,GenderEdit.OnButtonClickListener,UserIfmEdit.OnButtonClickListener{
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);


        UserIfm userIfm = new UserIfm();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment,userIfm);
        transaction.commit();

        //顶部返回按钮点击事件
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onButtonClick(String type) {
        if(type.equals("user_name")){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            UserIfmEdit userIfmEdit = new UserIfmEdit();
            Bundle bundle = new Bundle();
            bundle.putString("type",type);
            userIfmEdit.setArguments(bundle);
            transaction.replace(R.id.fragment_entire,userIfmEdit);
            transaction.commit();
        } else if (type.equals("user_qm")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            UserIfmEdit userIfmEdit = new UserIfmEdit();
            Bundle bundle = new Bundle();
            bundle.putString("type",type);
            userIfmEdit.setArguments(bundle);
            transaction.replace(R.id.fragment_entire,userIfmEdit);
            transaction.commit();
        }else if(type.equals("gender")){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            GenderEdit genderEdit = new GenderEdit();
            transaction.replace(R.id.fragment_entire,genderEdit);
            transaction.commit();
        }
    }

    @Override
    public void genderEdit() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        UserIfm userIfm = new UserIfm();
        transaction.replace(R.id.fragment,userIfm);
        transaction.commit();
    }

    @Override
    public void userIfmEdit() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        UserIfm userIfm = new UserIfm();
        transaction.replace(R.id.fragment,userIfm);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.example.yanyuan.MY_ACTION");
        sendBroadcast(intent);
    }
}