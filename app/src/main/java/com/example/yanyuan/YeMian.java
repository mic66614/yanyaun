package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class YeMian extends AppCompatActivity {

    ImageView back;
    TextView name_ym,search;
    String yemian;
    EditText key_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ye_mian);

        //获取页面传值,并设置页面标题
        Bundle bundle = getIntent().getExtras();
        yemian = bundle.getString("yemian");
        name_ym = (TextView) findViewById(R.id.name_ym);
        name_ym.setText(yemian);



        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        //特殊页面设置
        if(yemian.equals("搜索页面")){
            SearchHistory searchHistory = new SearchHistory();
            transaction.add(R.id.fragment,searchHistory);
            transaction.commit();
        } else {
            createSearch(null);
        }

        //设置返回按钮点击事件
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //搜索功能实现
        search = (TextView) findViewById(R.id.search);
        key_search = (EditText) findViewById(R.id.key_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = key_search.getText().toString();
                if(key.isEmpty()){
                    Toast.makeText(YeMian.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }else {
                    createSearch(key);
                }
                // 清除搜索框焦点
                search.clearFocus();
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            }
        });
    }

    public void createSearch(String key){
        SectionSearch sectionSearch = new SectionSearch();
        Bundle bundle1 = new Bundle();
        bundle1.putString("type",yemian);
        bundle1.putString("key",key);
        sectionSearch.setArguments(bundle1);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.fragment,sectionSearch);
        transaction1.commit();
    }
}