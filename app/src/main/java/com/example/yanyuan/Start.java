package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 使用Handler延迟3秒执行跳转到首页
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里编写跳转到另一个Activity的代码
                Intent intent = new Intent(Start.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 延迟时间，单位为毫秒，3000毫秒即3秒
    }
}