package com.example.yanyuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post extends AppCompatActivity {
    ImageView back,add;
    CardView post;
    EditText title,zhengwen;
    Spinner pinDao;
    LinearLayout section_picture;
    MyOpenHelper helper;
    String sql,pindao;
    List<String>  pictures = new ArrayList<>();
    List<Uri>  pictureUris = new ArrayList<>();
    List<String>  pinDaos = new ArrayList<>();
    // PICK_IMAGE_REQUEST是一个int常量，用于在onActivityResult中识别这个请求
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pinDao = findViewById(R.id.pindao);
        setPinDao();
        pinDao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pindao = pinDaos.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    pindao = pinDaos.get(0);
            }
        });

         // 上传图片功能实现
         add = (ImageView) findViewById(R.id.add);
         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent();
                 // 显示所有图片类型的intent
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_PICK);

                 // 启动图片选择器
                 startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_PICK_IMAGE);
             }
         });

         // 发布按钮点击事件
         post = (CardView) findViewById(R.id.post);
         post.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 title = (EditText) findViewById(R.id.title);
                 zhengwen = (EditText) findViewById(R.id.zhengwen);


                 String Title = title.getText().toString();
                 String Zhengwen = zhengwen.getText().toString();

                 if(Title.isEmpty()){
                     Toast.makeText(Post.this, "标题为空", Toast.LENGTH_SHORT).show();
                 } else if (Zhengwen.isEmpty()) {
                     Toast.makeText(Post.this, "正文为空", Toast.LENGTH_SHORT).show();
                 }else {
                     helper = new MyOpenHelper(getApplicationContext());
                     SQLiteDatabase db = helper.getWritableDatabase();
                     //获取当前登录的用户
                     SharedPreferences sharedPreferences = getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
                     String Username = sharedPreferences.getString("userName",null);

                     // 获取当前时间的毫秒数
                     long currentTimeMillis = System.currentTimeMillis();
                     // 将毫秒数转换为秒
                     long currentTimeSeconds = currentTimeMillis / 1000;

                     //保存图片路径
                     String picture = "";
                     for (int i = 0; i < pictures.size(); i++) {
                         if (i == 0){
                             picture = picture + pictures.get(i);
                         }else {
                             picture = picture+","+pictures.get(i);
                         }
                     }


                     //保存图片到文件夹
                     for (int i = 0; i < pictureUris.size(); i++) {
                         // 从Uri获取图片
                         InputStream inputStream = null;
                         try {
                             // 从Uri获取图片
                             inputStream = getContentResolver().openInputStream(pictureUris.get(i));
                             Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                             //获取文件名
                             String imageFileName = pictures.get(i);

                             // 指定保存图片的目录（这里使用内部存储）
                             // 获取应用专属的图片目录
                             File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                             File image = new File(storageDir, imageFileName);

                             // 保存图片
                             OutputStream os = new FileOutputStream(image);
                             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                             os.flush();
                             os.close();
                         } catch (IOException e) {
                             e.printStackTrace();
                             Toast.makeText(Post.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                         }
                     }
                     sql = "insert into tiezi(user_name,pindao,title,zhengwen,time,picture,picture_type) values('"+Username+"','"+pindao+"','"+Title+"','"+Zhengwen+"',"+currentTimeSeconds+",'"+picture+"','picture');";
                     db.execSQL(sql);

                     db.close();
                     Toast.makeText(Post.this, "发布成功", Toast.LENGTH_SHORT).show();
                     finish();
                 }
             }
         });
    }

    public void setPinDao(){
        helper = new MyOpenHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        sql = "select name from pindao";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            pinDaos.add(cursor.getString(0));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pinDaos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinDao.setAdapter(adapter);

        db.close();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            //保存图片uri
            pictureUris.add(imageUri);

            //设置图片名称
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "image_" + timeStamp + ".jpg";
            //保存图片到列表
            pictures.add(imageFileName);

            //显示图片
            section_picture = (LinearLayout) findViewById(R.id.section_picture);
            //创建一个新的图片控件
            ImageView imageView = new ImageView(this);

            //设置图片,使用Glide加载图片
            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);
            section_picture.addView(imageView);
        }
    }
}