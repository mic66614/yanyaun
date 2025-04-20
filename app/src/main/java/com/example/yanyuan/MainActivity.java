package com.example.yanyuan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ShouYe.OnButtonClickListener{

    BottomNavigationView bottomNavigationView;
    ShouYe shouye = new ShouYe();
    My my = new My();
    PinDao pinDao = new PinDao();
    FaXian faXian = new FaXian();
    MyOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new MyOpenHelper(this);
        helper.copyDataBase();

        //接收广播
        IntentFilter filter = new IntentFilter("com.example.yanyuan.MY_ACTION");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(myReceiver,filter,Context.RECEIVER_EXPORTED);
        }

        //判断用户是否登录,没有登录跳转登录页面
        ifLogin();

        //底部导航
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setItemIconTintList(null);
    }

    //Fragment替换函数
    void FragmentReplace(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment,fragment); //参数1:目标fragment控件的id,参数2:要添加的fragment
        transaction.commit(); //事务创建完毕后必须提交才会执行
    }

    //底部导航点击事件处理
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (item.getItemId() == R.id.bot_nav_shouye ) {
            // 处理首页点击事件
            FragmentReplace(shouye);
            return true;
        } else if(item.getItemId() == R.id.bot_nav_pindao){
            // 处理频道点击事件
            FragmentReplace(pinDao);
            return true;
        } else if (item.getItemId() == R.id.bot_nav_faxian) {
            // 处理发现点击事件
            FragmentReplace(faXian);
            return true;
        } else if (item.getItemId() == R.id.bot_nav_my) {
            // 处理我的点击事件
            FragmentReplace(my);
            return true;
        }else {
            // 处理发帖点击事件
            Intent intent = new Intent(MainActivity.this,Post.class);
            startActivity(intent);
            return true;
        }
    };

    private void ifLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("ifLogin",MODE_PRIVATE);
        int flag = sharedPreferences.getInt("ifLogin",0);
        if(flag == 0){
            //没有登录，跳转到登录页面
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            return;
        }
    }


    //首页my跳转个人中心
    @Override
    public void onButtonClick() {
        bottomNavigationView.setSelectedItemId(R.id.bot_nav_my);
    }

    //BroadcastReceiver，用于接收来的广播
    private android.content.BroadcastReceiver  myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bottomNavigationView.setSelectedItemId(R.id.bot_nav_my);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}