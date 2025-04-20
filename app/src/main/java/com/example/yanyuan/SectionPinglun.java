package com.example.yanyuan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectionPinglun#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionPinglun extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    MyOpenHelper helper;

    public SectionPinglun() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionPinglun.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionPinglun newInstance(String param1, String param2) {
        SectionPinglun fragment = new SectionPinglun();
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
        View view = inflater.inflate(R.layout.fragment_section_pinglun, container, false);

        Bundle bundle = getArguments();
        int id = bundle.getInt("id");

        helper = new MyOpenHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "SELECT * FROM pinglun where tid = "+id+" ORDER BY time DESC;";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            int Pid = cursor.getInt(0);
            String user_name = cursor.getString(2);
            String pinglun = cursor.getString(3);

            //获取并转换时间
            int timestamp = cursor.getInt(4);
            String time = convertTimestamp(timestamp);

            //根据用户名获取用户头像
            sql = "select * from user where user_name = '"+user_name+"'";
            Cursor cursor1 = db.rawQuery(sql,null);
            cursor1.moveToNext();
            String img = cursor1.getString(5);
            String img_type = cursor1.getString(9);

            //创建添加评论Fragment，并向其传值
            Pinglun pinglun1 = new Pinglun();
            Bundle bundle1 = new Bundle();
            bundle1.putString("user_name",user_name);
            bundle1.putString("pinglun",pinglun);
            bundle1.putString("time",time);
            bundle1.putString("img",img);
            bundle1.putString("img_type",img_type);
            pinglun1.setArguments(bundle1);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.section_pinglun,pinglun1);
            transaction.commit();
        }

        return view;
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
}