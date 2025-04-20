package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.security.PublicKey;

public class TieZi2 extends AppCompatActivity implements PinglunEdit.OnFragmentInteractionListener{
    TextView pindao,title,user_name,time,zhengwen,count_dianzan,count_shoucang,count_pinglun,guanzhu;
    LinearLayout picture_layout,section_pinglun;
    ImageView back,dianzan,shoucang,user_tx;
    EditText pinglun;
    MyOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tie_zi2);

        helper = new MyOpenHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        pindao = (TextView) findViewById(R.id.pindao);
        title = (TextView) findViewById(R.id.title);
        user_name = (TextView) findViewById(R.id.user_name);
        time = (TextView) findViewById(R.id.time);
        zhengwen = (TextView) findViewById(R.id.zhengwen);
        back = (ImageView) findViewById(R.id.back);
        picture_layout = (LinearLayout) findViewById(R.id.picture_layout);
        user_tx = (ImageView) findViewById(R.id.user_tx);
        guanzhu = (TextView) findViewById(R.id.guanzhu);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");
        String Time = bundle.getString("time");
        String[] pictures = bundle.getStringArray("pictures");
        String img = bundle.getString("img");
        String img_type = bundle.getString("img_type");
        int gzFlag = bundle.getInt("flag");
        //设置关注情况
        if (gzFlag == 0){
            guanzhu.setText("+关注");
        }else {
            guanzhu.setText("已关注");
        }


        //帖子数据获取并设置
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "SELECT * FROM tiezi where id = "+id+";";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null || cursor.getCount()==0){
            Toast.makeText(this, "当前帖子已被删除", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.moveToNext();
        String User_name = cursor.getString(1);
        String Pindao = cursor.getString(2);
        String Title = cursor.getString(3);
        String Zhengwen = cursor.getString(4);
        int Count_dianzan = cursor.getInt(7);
        int Count_shoucang = cursor.getInt(8);
        int Count_pinglun = cursor.getInt(9);
        String picture_type = cursor.getString(11);

        pindao.setText(Pindao);
        title.setText(Title);
        user_name.setText(User_name);
        time.setText(Time);
        zhengwen.setText(Zhengwen);
        //设置头像
        if(img.isEmpty()){
            //如果没有头像显示默认头像
            int resourceId = getResources().getIdentifier("head", "drawable", getPackageName());
            user_tx.setImageResource(resourceId);
        }else {
            if (img_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(img, "drawable", getPackageName());
                user_tx.setImageResource(resourceId);
            }else {
                // 获取应用专属的图片目录
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                // 构建图片的完整路径
                File imageFile = new File(storageDir,img);

                // 检查文件是否存在
                if (!imageFile.exists()) {
                    // 处理文件不存在的情况
                    Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
                }

                // 解码图片文件为一个Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                //显示图片
                user_tx.setImageBitmap(bitmap);
            }
        }
        //用户头像点击事件
        user_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_name",User_name);
                Intent intent = new Intent(TieZi2.this, User2.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        //图片加载
        if(pictures != null){
            if (picture_type.equals("drawable")){
                for (String picture : pictures) {
                    //创建一个新的图片控件
                    ImageView imageView = new ImageView(this);
                    int resourceId = getResources().getIdentifier(picture, "drawable", getPackageName());
                    //设置图片,使用Glide加载图片
                    Glide.with(this)
                            .load(resourceId)
                            .into(imageView);
                    picture_layout.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Picture picture1 = new Picture();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("picture",picture);
                            bundle1.putString("picture_type",picture_type);
                            picture1.setArguments(bundle1);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.fragment_entire,picture1);
                            transaction.commit();
                        }
                    });
                }
            }else {
                //显示图片
                for (String picture : pictures) {
                    //创建一个新的图片控件
                    ImageView imageView = new ImageView(this);
                    // 获取应用专属的图片目录
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                    // 构建图片的完整路径
                    File imageFile = new File(storageDir,picture);

                    // 检查文件是否存在
                    if (!imageFile.exists()) {
                        // 处理文件不存在的情况
                        Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
                    }

                    //设置图片,使用Glide加载图片
                    Glide.with(this)
                            .load(imageFile)
                            .into(imageView);
                    picture_layout.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Picture picture1 = new Picture();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("picture",picture);
                            bundle1.putString("picture_type",picture_type);
                            picture1.setArguments(bundle1);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.fragment_entire,picture1);
                            transaction.commit();
                        }
                    });
                }
            }
        }

        //每次进入帖子浏览量增加
        sql = "update tiezi set liulan = liulan+1 where id = "+id+";";
        db.execSQL(sql);

        //每次进入增加浏览记录
        sql = "select * from liulanjilu where user_name = '"+Username+"' and tid = "+id+";";
        Cursor cursor1 = db.rawQuery(sql,null);
        if (cursor.getCount() == 0){
            //如果之前浏览记录没有这个帖子，则直接加入表内
            sql = "insert into liulanjilu(user_name,tid) values('"+Username+"',"+id+");";
            db.execSQL(sql);
        }else {
            //如果之前浏览记录有这个帖子，则先删除原来的，再新加入表内
            sql  = "delete from liulanjilu where user_name = '"+Username+"' and tid = "+id+";";
            db.execSQL(sql);
            sql = "insert into liulanjilu(user_name,tid) values('"+Username+"',"+id+");";
            db.execSQL(sql);
        }

        //设置关注点击事件
        //获取当前登录的用户
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String gFlag = guanzhu.getText().toString();
                if (Username.equals(User_name)){
                    Toast.makeText(TieZi2.this, "不能关注自己哦!", Toast.LENGTH_SHORT).show();
                }else {
                    if(gFlag.equals("已关注")){
                        //当前是关注状态
                        String sql = "delete from guanzhu where user = '"+Username+"' and b_user = '"+User_name+"'";
                        db.execSQL(sql);
                        sql = "update user set fensi = fensi - 1 where user_name = '"+User_name+"'";
                        db.execSQL(sql);
                        sql = "update user set guanzhu = guanzhu - 1 where user_name = '"+Username+"'";
                        db.execSQL(sql);
                        guanzhu.setText("+关注");
                    }else {
                        //当前是未关注状态
                        String sql = "insert into guanzhu(user,b_user) values('"+Username+"','"+User_name+"');";
                        db.execSQL(sql);
                        sql = "update user set fensi = fensi + 1 where user_name = '"+User_name+"'";
                        db.execSQL(sql);
                        sql = "update user set guanzhu = guanzhu + 1 where user_name = '"+Username+"'";
                        db.execSQL(sql);
                        guanzhu.setText("已关注");
                    }
                }
                db.close();
            }
        });

        //评论区显示
        Bundle bundle1 = new Bundle();
        bundle1.putInt("id",id);
        SectionPinglun sectionPinglun = new SectionPinglun();
        sectionPinglun.setArguments(bundle1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.section_pinglun,sectionPinglun);
        transaction.commit();

        //顶部返回键点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //底部区域点击事件
        pinglun = (EditText) findViewById(R.id.pinglun);
        pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinglunEdit pinglunEdit = new PinglunEdit();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("tid",id);
                bundle2.putString("user_name",User_name);
                pinglunEdit.setArguments(bundle2);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_entire,pinglunEdit);
                transaction.commit();
            }
        });

        //点赞评论收藏数量设置
        count_dianzan = (TextView) findViewById(R.id.count_dianzan);
        count_pinglun = (TextView) findViewById(R.id.count_pinglun);
        count_shoucang = (TextView) findViewById(R.id.count_shoucang);
        count_dianzan.setText(String.valueOf(Count_dianzan));
        count_pinglun.setText(String.valueOf(Count_pinglun));
        count_shoucang.setText(String.valueOf(Count_shoucang));

        dianzan = (ImageView) findViewById(R.id.dianzan);
        shoucang = (ImageView) findViewById(R.id.shoucang);
        int resourceId = getResources().getIdentifier("dz_hou", "drawable", getPackageName());
        int resourceId2 = getResources().getIdentifier("dz_qian", "drawable", getPackageName());
        int resourceId3 = getResources().getIdentifier("sc_hou", "drawable", getPackageName());
        int resourceId4 = getResources().getIdentifier("sc_qian", "drawable", getPackageName());
        if (ifDianzan(Username,id) == 1){
            //如果有点赞数据，那么将点赞图片设置为已经点过的
            dianzan.setImageResource(resourceId);
        }else {
            //如果没有点赞数据，那么将点赞图片设置为未点过的
            dianzan.setImageResource(resourceId2);
        };

        if (ifShoucang(Username,id) == 1){
            //如果有收藏数据，那么将收藏图片设置为已经点过的
            shoucang.setImageResource(resourceId3);
        }else {
            //如果没有收藏数据，那么将收藏图片设置为未点过的
            shoucang.setImageResource(resourceId4);
        };

        dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "select * from dianzan where user_name = '"+Username+"' and id = "+id+";";
                Cursor cursor = db.rawQuery(sql,null);
                //如果当前没有点赞
                if(cursor == null || cursor.getCount()==0){
                    dianzan.setImageResource(resourceId);
                    sql = "insert into dianzan(user_name,id) values('"+Username+"',"+id+");";
                    db.execSQL(sql);
                    sql = "update tiezi set dianzan = dianzan+1 where id = "+id+";";
                    db.execSQL(sql);
                }else {
                    //如果当前已经点赞
                    sql = "delete from dianzan where user_name = '"+Username+"' and id = "+id+";";
                    db.execSQL(sql);
                    sql = "update tiezi set dianzan = dianzan-1 where id = "+id+";";
                    db.execSQL(sql);
                }
                if(ifDianzan(Username,id) == 0){
                    //如果没有点赞数据，那么将点赞图片设置为没有点过的
                    dianzan.setImageResource(resourceId2);
                }else {
                    //如果有点赞数据，那么将点赞图片设置为已经点过的
                    dianzan.setImageResource(resourceId);
                }
                sql = "select dianzan from tiezi where id = "+id+";";
                Cursor cursor1 = db.rawQuery(sql,null);
                cursor1.moveToNext();
                int Count_dianzan = cursor1.getInt(0);
                count_dianzan.setText(String.valueOf(Count_dianzan));
                db.close();
            }
        });

        shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "select * from shoucang where user_name = '"+Username+"' and id = "+id+";";
                Cursor cursor = db.rawQuery(sql,null);
                //如果当前没有收藏
                if(cursor == null || cursor.getCount()==0){
                    shoucang.setImageResource(resourceId3);
                    sql = "insert into shoucang(user_name,id) values('"+Username+"',"+id+");";
                    db.execSQL(sql);
                    sql = "update tiezi set shoucang = shoucang+1 where id = "+id+";";
                    db.execSQL(sql);
                }else {
                    //如果当前已经收藏
                    sql = "delete from shoucang where user_name = '"+Username+"' and id = "+id+";";
                    db.execSQL(sql);
                    sql = "update tiezi set shoucang = shoucang-1 where id = "+id+";";
                    db.execSQL(sql);
                }
                if(ifShoucang(Username,id) == 0){
                    //如果没有收藏数据，那么将点赞图片设置为没有点过的
                    shoucang.setImageResource(resourceId4);
                }else {
                    //如果有收藏数据，那么将点赞图片设置为已经点过的
                    shoucang.setImageResource(resourceId3);
                }
                sql = "select shoucang from tiezi where id = "+id+";";
                Cursor cursor1 = db.rawQuery(sql,null);
                cursor1.moveToNext();
                int Count_shoucang = cursor1.getInt(0);
                count_shoucang.setText(String.valueOf(Count_shoucang));
                db.close();
            }
        });

        //主页面打开的数据库关闭
        db.close();
    }
    public int ifDianzan(String Username,int id){
        //点赞状态判断
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from dianzan where user_name = '"+Username+"' and id = "+id+";";
        Cursor cursor2 = db.rawQuery(sql,null);
        if(cursor2 == null || cursor2.getCount()==0){
            return 0;
        }else {
            return 1;
        }
    }

    public int ifShoucang(String Username,int id){
        //收藏状态判断
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from shoucang where user_name = '"+Username+"' and id = "+id+";";
        Cursor cursor2 = db.rawQuery(sql,null);
        if(cursor2 == null || cursor2.getCount()==0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public void onFragmentReplaceRequested() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 评论区更新
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");
        Bundle bundle1 = new Bundle();
        bundle1.putInt("id",id);
        SectionPinglun sectionPinglun = new SectionPinglun();
        sectionPinglun.setArguments(bundle1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.section_pinglun,sectionPinglun);
        transaction.commit();

        //更新评论数
        helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select pinglun from tiezi where id = "+id+";";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        int Count_pinglun = cursor.getInt(0);
        count_pinglun.setText(String.valueOf(Count_pinglun));
    }
}