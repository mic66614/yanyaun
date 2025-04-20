package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    ImageView back;
    CardView qiehuan;
    TextView text;
    int flag = 0; //定义切换标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //顶部返回键点击事件
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //切换按钮点击事件
        qiehuan = (CardView) findViewById(R.id.qiehuan);
        text = (TextView) findViewById(R.id.text);
        qiehuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(flag == 0){
                    FReg fReg = new FReg();
                    transaction.replace(R.id.fragment,fReg);
                    text.setText("切换登录");
                    flag = 1;
                }else {
                    FLogin fLogin = new FLogin();
                    transaction.replace(R.id.fragment,fLogin);
                    flag = 0;
                    text.setText("切换注册");
                }
                transaction.commit();
            }
        });
    }
}