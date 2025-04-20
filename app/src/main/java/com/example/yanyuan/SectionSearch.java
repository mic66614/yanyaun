package com.example.yanyuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectionSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionSearch extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView tip;
    LinearLayout fragment;
    MyOpenHelper helper;

    public SectionSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionSearch newInstance(String param1, String param2) {
        SectionSearch fragment = new SectionSearch();
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
        View view = inflater.inflate(R.layout.fragment_section_search, container, false);
        fragment = view.findViewById(R.id.fragment);
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        String key = bundle.getString("key");

        helper = new MyOpenHelper(getActivity());
        if (type.equals("我的关注") || type.equals("我的粉丝")){
            searchUser(type,key);
        }else {
            searchTiezi(type,key);
        }

        return view;
    }

    public void SetTip(){
        TextView tip = new TextView(getActivity());
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

    public void searchTiezi(String type,String key){
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        boolean flag = false;
        String [] pindaos = {"体育","热点","音乐","游戏","影视","公告"};
        for (String pindao : pindaos) {
            if (pindao.equals(type)) {
                flag = true;
                break;
            }
        }
        String sql = null;
        if(type.equals("我的帖子") && key == null){
            sql = "SELECT * FROM tiezi where user_name = '"+Username+"';";
        } else if (type.equals("我的帖子")) {
            sql = "SELECT * FROM tiezi where user_name = '"+Username+"' and (title LIKE '%"+key+"%' OR zhengwen LIKE '%"+key+"%';)";
        } else if (type.equals("搜索页面")) {
            sql = "SELECT * FROM tiezi where title LIKE '%"+key+"%' OR zhengwen LIKE '%"+key+"%';";
        } else if (type.equals("热门帖子") && key == null) {
            sql = "SELECT * FROM tiezi ORDER BY liulan DESC;";
        } else if (type.equals("热门帖子")) {
            sql = "SELECT * FROM tiezi where title LIKE '%"+key+"%' OR zhengwen LIKE '%"+key+"%' ORDER BY liulan DESC;";
        } else if (type.equals("最新帖子") && key == null) {
            sql = "SELECT * FROM tiezi ORDER BY time DESC;";
        } else if (type.equals("最新帖子")) {
            sql = "SELECT * FROM tiezi where title LIKE '%"+key+"%' OR zhengwen LIKE '%"+key+"%' ORDER BY time DESC;";
        } else if (type.equals("浏览记录") && key == null) {
            sql = "SELECT t.*  FROM tiezi t INNER JOIN liulanjilu l ON t.id = l.tid where l.user_name = '"+Username+"' ORDER BY l.id DESC";
        } else if (type.equals("浏览记录")) {
            sql = "SELECT t.*  FROM tiezi t INNER JOIN liulanjilu l ON t.id = l.tid where l.user_name = '"+Username+"' and (t.title LIKE '%"+key+"%' OR t.zhengwen LIKE '%"+key+"%') ORDER BY l.id DESC";
        } else if (type.equals("我的收藏") && key == null) {
            sql = "SELECT t.*  FROM tiezi t INNER JOIN shoucang s ON t.id = s.id where s.user_name = '"+Username+"'";
        } else if (type.equals("我的收藏")) {
            sql = "SELECT t.*  FROM tiezi t INNER JOIN shoucang s ON t.id = s.id where s.user_name = '"+Username+"' and (t.title LIKE '%"+key+"%' OR t.zhengwen LIKE '%"+key+"%')";
        } else if (flag && key == null){
            sql = "select * from tiezi where pindao = '"+type+"'";
        } else {
            sql = "select * from tiezi where pindao = '"+type+"' and (title LIKE '%"+key+"%' OR zhengwen LIKE '%"+key+"%');";
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
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

                //获取关注数据
                //获取当前登录的用户
                sql = "select * from guanzhu where user = '"+Username+"' and b_user = '"+user_name+"'";
                Cursor cursor3 = db.rawQuery(sql,null);
                int flag1;
                if(cursor3.getCount() == 0){
                    flag1 = 0;
                }else {
                    flag1 = 1;
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
                bundle.putString("picture_type",picture_type);
                bundle.putInt("liulan",liulan);
                bundle.putInt("dianzan",dianzan);
                bundle.putInt("shoucang",shoucang);
                bundle.putInt("pinglun",pinglun);
                bundle.putStringArray("pictures",pictures);
                bundle.putInt("flag",flag1);

                tieZi.setArguments(bundle);
                transaction.add(R.id.fragment,tieZi);
            }
            transaction.commit();
        }
        db.close();
    }

    public void searchUser(String type,String key){
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        String sql = null;

        if(type.equals("我的关注") && key == null){
            sql = "SELECT u.*  FROM user u INNER JOIN guanzhu g ON u.user_name = g.b_user where g.user = '"+Username+"'";
        } else if (type.equals("我的关注")) {
            sql = "SELECT u.*  FROM user u INNER JOIN guanzhu g ON u.user_name = g.b_user where g.user = '"+Username+"' and g.b_user LIKE '%"+key+"%'";
        } else if (type.equals("我的粉丝") && key == null) {
            sql = "SELECT u.*  FROM user u INNER JOIN guanzhu g ON u.user_name = g.user where g.b_user = '"+Username+"'";
        } else if(type.equals("我的粉丝")){
            sql = "SELECT u.*  FROM user u INNER JOIN guanzhu g ON u.user_name = g.user where g.b_user = '"+Username+"' and g.user LIKE '%"+key+"%'";
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null || cursor.getCount()==0){
            SetTip();
        }else {
            while (cursor.moveToNext()){
                String user_name = cursor.getString(1);
                String qianming = cursor.getString(4);
                String img = cursor.getString(5);
                String img_type = cursor.getString(9);

                User user = new User();
                Bundle bundle = new Bundle();
                bundle.putString("user_name",user_name);
                bundle.putString("qianming",qianming);
                bundle.putString("img",img);
                bundle.putString("img_type",img_type);
                user.setArguments(bundle);
                transaction.add(R.id.fragment,user);
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
}