package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class User2 extends AppCompatActivity {
    ImageView back,user_img;
    TextView user_name,user_qm,count_fensi,count_guanzhu,count_jinbi,guanzhu;
    MyOpenHelper helper = new MyOpenHelper(this);
    String sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        //顶部返回按钮点击事件
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //获取传递的用户名
        Bundle bundle = getIntent().getExtras();
        String User_name = bundle.getString("user_name");

        getUser(User_name);
        getTiezi(User_name);

    }

    public void getUser(String User_name){
        user_name = (TextView) findViewById(R.id.user_name);
        user_img = (ImageView) findViewById(R.id.user_img);
        user_qm = (TextView) findViewById(R.id.user_qm);
        count_fensi = (TextView) findViewById(R.id.count_fensi);
        count_guanzhu = (TextView) findViewById(R.id.count_guanzhu);
        count_jinbi = (TextView) findViewById(R.id.count_jinbi);
        guanzhu = (TextView) findViewById(R.id.guanzhu);


        SQLiteDatabase db = helper.getWritableDatabase();
        sql = "select * from user where user_name = '"+User_name+"'";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        String qianming = cursor.getString(4);
        String img = cursor.getString(5);
        int c_jinbi = cursor.getInt(6);
        int c_guanzhu = cursor.getInt(7);
        int c_fensi = cursor.getInt(8);
        String img_type = cursor.getString(9);

        //设置用户名
        user_name.setText(User_name);


        //设置签名
        if(qianming.isEmpty()){
            user_qm.setText("还没有设置签名哦！");
        }else {
            user_qm.setText(qianming);
        }

        //设置头像
        if(img.isEmpty()){
            //如果没有头像显示默认头像
            int resourceId = getResources().getIdentifier("head", "drawable", getPackageName());
            user_img.setImageResource(resourceId);
        }else {
            if (img_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(img, "drawable", getPackageName());
                user_img.setImageResource(resourceId);
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
                user_img.setImageBitmap(bitmap);
            }
        }


        //设置粉丝、关注、金币数量
        count_fensi.setText(String.valueOf(c_fensi));
        count_guanzhu.setText(String.valueOf(c_guanzhu));
        count_jinbi.setText(String.valueOf(c_jinbi));

        //获取登录用户
        SharedPreferences sharedPreferences = getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        //获取关注数据
        sql = "select * from guanzhu where user = '"+Username+"' and b_user = '"+User_name+"'";
        Cursor cursor1 = db.rawQuery(sql,null);
        int flag;
        if(cursor1.getCount() == 0){
            guanzhu.setText("关注");
        }else {
            guanzhu.setText("已关注");
        }


        //设置关注点击事件
        //获取当前登录的用户
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String gFlag = guanzhu.getText().toString();
                if (Username.equals(User_name)){
                    Toast.makeText(User2.this, "不能关注自己哦!", Toast.LENGTH_SHORT).show();
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
        db.close();
    }

    public void getTiezi(String User_name){
        sql = "select * from tiezi where user_name = '"+User_name+"'";
        SQLiteDatabase db = helper.getWritableDatabase();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null || cursor.getCount()==0){
            SetTip();
        }else {
            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                String user_name = cursor.getString(1);
                String pindao = cursor.getString(2);
                String title = cursor.getString(3);
                String zhengwen = cursor.getString(4);
                long timestamp = cursor.getInt(5);
                int liulan = cursor.getInt(6);
                int dianzan = cursor.getInt(7);
                int shoucang = cursor.getInt(8);
                int pinglun = cursor.getInt(9);
                String S_picture = cursor.getString(10);
                String picture_type = cursor.getString(11);

                //图片路径分隔
                String[] pictures;
                if(S_picture == null){
                    pictures = null;
                }else {
                    pictures = S_picture.split(",");
                }
                //时间戳转换为具体时间
                String time = convertTimestamp(timestamp);

                //获取用户头像
                sql = "select * from user where user_name='"+user_name+"';";
                Cursor cursor2 = db.rawQuery(sql,null);
                cursor2.moveToNext();
                String img = cursor2.getString(5);
                String img_type = cursor2.getString(9);
                //设置头像
                //设置头像
                if(img.isEmpty()){
                    //如果没有头像显示默认头像
                    int resourceId = getResources().getIdentifier("head", "drawable", getPackageName());
                    user_img.setImageResource(resourceId);
                }else {
                    if (img_type.equals("drawable")){
                        int resourceId = getResources().getIdentifier(img, "drawable", getPackageName());
                        user_img.setImageResource(resourceId);
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
                        user_img.setImageBitmap(bitmap);
                    }
                }

                TieZi tieZi = new TieZi();
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                bundle.putString("user_name",user_name);
                bundle.putString("pindao",pindao);
                bundle.putString("title",title);
                bundle.putString("zhengwen",zhengwen);
                bundle.putString("time",time);
                bundle.putString("img",img);
                bundle.putString("img_type",img_type);
                bundle.putInt("liulan",liulan);
                bundle.putInt("dianzan",dianzan);
                bundle.putInt("shoucang",shoucang);
                bundle.putInt("pinglun",pinglun);
                bundle.putStringArray("pictures",pictures);
                bundle.putString("picture_type",picture_type);

                tieZi.setArguments(bundle);
                transaction.add(R.id.fragment,tieZi);
            }
            transaction.commit();
        }
        db.close();
    }

    public static String convertTimestamp(long timestamp) {
        // 使用Instant类将秒级时间戳转换为Instant对象
        Instant instant = Instant.ofEpochSecond(timestamp);
        // 将Instant对象转换为系统默认时区的LocalDateTime对象
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        // 获取系统当前时间戳
        LocalDateTime now = LocalDateTime.now();

        // 计算当前时间与发帖时间LocalDateTime对象之间的秒数差
        long seconds = ChronoUnit.SECONDS.between(localDateTime, now);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        // 根据时间差的不同，返回不同的时间字符串
        if(seconds < 60){ // 1分钟内
            return seconds + "秒前";
        }else if (seconds < 60 * 60) { // 1小时内
            return minutes + "分钟前";
        } else if (hours < 24) { // 1天内
            return hours + "小时前";
        } else if (days < 3) { // 3天内
            return days + "天前";
        } else {
            // 超过3天，显示具体日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return localDateTime.format(formatter);
        }
    }

    public void SetTip(){
        LinearLayout fragment = (LinearLayout) findViewById(R.id.fragment);
        TextView tip = new TextView(this);
        tip.setText("未找到数据");
        tip.setTextSize(20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER; // 设置TextView在LinearLayout中居中
        // 将布局参数应用到TextView上
        tip.setLayoutParams(layoutParams);
        // 将TextView添加到LinearLayout中
        fragment.addView(tip);
    }
}