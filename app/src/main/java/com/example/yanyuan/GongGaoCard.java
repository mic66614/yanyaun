package com.example.yanyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GongGaoCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GongGaoCard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView picture;
    TextView title,time;
    LinearLayout card;

    public GongGaoCard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GongGaoCard.
     */
    // TODO: Rename and change types and number of parameters
    public static GongGaoCard newInstance(String param1, String param2) {
        GongGaoCard fragment = new GongGaoCard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gong_gao_card, container, false);

        Bundle bundle = getArguments();
        int id = bundle.getInt("id");
        picture = view.findViewById(R.id.picture);
        title = view.findViewById(R.id.title);
        time = view.findViewById(R.id.time);
        card = view.findViewById(R.id.card);
        Load(id);

        return view;
    }

    public void Load(int id){
        MyOpenHelper helper = new MyOpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from tiezi where id = "+id+";";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        String user_name = cursor.getString(1);
        String Title = cursor.getString(3);
        long timestamp = cursor.getInt(5);
        String S_picture = cursor.getString(10);
        String picture_type = cursor.getString(11);

        //设置信息
        title.setText(Title);
        time.setText(convertTimestamp(timestamp));


        //图片路径分隔
        String[] pictures;
        if(S_picture == null){
            pictures = null;
        }else {
            pictures = S_picture.split(",");
        }
        // 获取图片的资源ID,第一张图片
        if(pictures == null){ //如果帖子没有图片，则设置为默认图片
            int resourceId = getResources().getIdentifier("top", "drawable", getActivity().getPackageName());
        }else {
            if (picture_type.equals("drawable")) {
                int resourceId = getResources().getIdentifier(pictures[0], "drawable", getActivity().getPackageName());
                //设置图片,使用Glide加载图片
                Glide.with(this)
                        .load(resourceId)
                        .into(picture);
            } else {
                // 获取应用专属的图片目录
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                // 构建图片的完整路径
                File imageFile = new File(storageDir, pictures[0]);

                // 检查文件是否存在
                if (!imageFile.exists()) {
                    // 处理文件不存在的情况
                    Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
                }

                // 解码图片文件为一个Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                //设置图片,使用Glide加载图片
                Glide.with(this)
                        .load(imageFile)
                        .into(picture);
            }
        }

        sql = "select * from user where user_name = '"+user_name+"';";
        Cursor cursor1 = db.rawQuery(sql,null);
        cursor1.moveToNext();
        String img = cursor1.getString(5);
        String img_type = cursor1.getString(9);

        //获取关注数据
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);
        sql = "select * from guanzhu where user = '"+Username+"' and b_user = '"+user_name+"'";
        Cursor cursor3 = db.rawQuery(sql,null);
        int flag;
        if(cursor3.getCount() == 0){
            flag = 0;
        }else {
            flag = 1;
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                bundle.putString("time",convertTimestamp(timestamp));
                bundle.putStringArray("pictures",pictures);
                bundle.putString("img",img);
                bundle.putString("img_type",img_type);
                bundle.putInt("flag",flag);
                Intent intent = new Intent(getActivity(), TieZi2.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        db.close();
    }

    //时间转换
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
}