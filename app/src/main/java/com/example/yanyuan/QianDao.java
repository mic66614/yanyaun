package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysql.jdbc.log.Log;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class QianDao extends AppCompatActivity {
    ImageView back;
    TextView days1,days2,days3,qindao;
    MyOpenHelper helper;
    String sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qian_dao);

        // 顶部返回键点击事件
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        days1 = (TextView) findViewById(R.id.days_1);
        days2 = (TextView) findViewById(R.id.days_2);
        days3 = (TextView) findViewById(R.id.days_3);

        //签到按钮点击事件
        qindao = (TextView) findViewById(R.id.qiandao);
        setDays();
        qindao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = qindao.getText().toString();
                if(flag.equals("立即签到")){
                    helper = new MyOpenHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    //获取当前登录的用户
                    SharedPreferences sharedPreferences = getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
                    String Username = sharedPreferences.getString("userName",null);

                    // 获取当前时间的毫秒数
                    long currentTimeMillis = System.currentTimeMillis();

                    sql = "update user set jinbi = jinbi+1,qiandao = qiandao+1,last_qiandao = "+currentTimeMillis+" where user_name = '"+Username+"'";
                    db.execSQL(sql);
                    db.close();
                    Toast.makeText(QianDao.this, "签到成功，金币+1", Toast.LENGTH_SHORT).show();
                    setDays();
                }else {
                    //已经签到过则无响应
                }
            }
        });
    }

    public void setDays(){
        helper = new MyOpenHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);
        sql = "select * from user where user_name = '"+Username+"'";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();

        //签到天数设置
        int days = cursor.getInt(10);
        String d1,d2,d3;
        if(days >= 10 && days < 100){
            d1 = String.valueOf(0);
            d2 = String.valueOf(days/10);
            d3 = String.valueOf(days%10);
        } else if (days >= 100 && days < 1000) {
            d1 = String.valueOf(days/100);
            d2 = String.valueOf((days/10)%10);
            d3 = String.valueOf(days%10);
        }else {
            d1 = String.valueOf(0);
            d2 = String.valueOf(0);
            d3 = String.valueOf(days);
        }
        days1.setText(d1);
        days2.setText(d2);
        days3.setText(d3);

        //签到状态设置
        long last_qiandao = cursor.getLong(11);

        // 获取今天0点和明天0点的时间戳
        long todayStartTimestamp = getTodayStartTimestamp();
        long tomorrowStartTimestamp = getTomorrowStartTimestamp();
        // 判断给定时间戳是否在今天0点和明天0点之间
        if (last_qiandao >= todayStartTimestamp && last_qiandao < tomorrowStartTimestamp) {
            // 上次签到时间在今天之内
            qindao.setText("已签到");
        } else {
            // 上次签到时间不在今天之内
            qindao.setText("立即签到");
        }
        db.close();
    }

    // 获取今天0点的时间戳
    public static long getTodayStartTimestamp() {
        LocalDate today = LocalDate.now();
        long timestampToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timestampToday;
    }

    // 获取明天0点的时间戳
    public static long getTomorrowStartTimestamp() {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        long timestampTomorrow = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timestampTomorrow;
    }
}