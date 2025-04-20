package com.example.yanyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShouYe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShouYe extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText search;
    ImageButton my;
    LinearLayout qiandao,remen,zuixin,fatie;
    MyOpenHelper helper;

    // 定义一个接口
    public interface OnButtonClickListener {
        void onButtonClick();
    }

    private OnButtonClickListener mListener;

    // 确保Activity实现了这个接口
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonClickListener) {
            mListener = (OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    // 当Fragment不再存在时，避免内存泄漏
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Shouye.
     */
    // TODO: Rename and change types and number of parameters
    public static ShouYe newInstance(String param1, String param2) {
        ShouYe fragment = new ShouYe();
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
        View view = inflater.inflate(R.layout.fragment_shou_ye, container, false);

        search = view.findViewById(R.id.search);
        search.setOnClickListener(this);

        my = view.findViewById(R.id.my);
        my.setOnClickListener(this);

        qiandao = view.findViewById(R.id.qiandao);
        qiandao.setOnClickListener(this);

        remen = view.findViewById(R.id.remen);
        remen.setOnClickListener(this);

        zuixin = view.findViewById(R.id.zuixin);
        zuixin.setOnClickListener(this);

        fatie = view.findViewById(R.id.fatie);
        fatie.setOnClickListener(this);

        helper = new MyOpenHelper(getActivity());



        //获取首页帖子
        getTiezi();
        return view;
    }

    public void getTiezi() {
        SQLiteDatabase db = helper.getWritableDatabase();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        String sql = "SELECT * FROM tiezi ORDER BY RANDOM();";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null || cursor.getCount()==0){
            Toast.makeText(getContext(), "未找到帖子", Toast.LENGTH_SHORT).show();
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
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin",Context.MODE_PRIVATE);
                String Username = sharedPreferences.getString("userName",null);
                sql = "select * from guanzhu where user = '"+Username+"' and b_user = '"+user_name+"'";
                Cursor cursor3 = db.rawQuery(sql,null);
                int flag;
                if(cursor3.getCount() == 0){
                    flag = 0;
                }else {
                    flag = 1;
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
                bundle.putInt("flag",flag);
                bundle.putStringArray("pictures",pictures);
                bundle.putString("picture_type",picture_type);

                tieZi.setArguments(bundle);
                transaction.add(R.id.fragment,tieZi);
            }
            transaction.commit();
        }
        db.close();
    }
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v.getId() == R.id.search){
            //搜索页面跳转
            bundle.putString("yemian","搜索页面");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.my) {
            //个人中心页面跳转
            toMy();
        } else if (v.getId() == R.id.qiandao) {
            //签到页面跳转
            Intent intent = new Intent(getActivity(), QianDao.class);
            startActivity(intent);
        } else if (v.getId() == R.id.remen) {
            //热门页面跳转
            bundle.putString("yemian","热门帖子");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.zuixin) {
            //最新页面跳转
            bundle.putString("yemian","最新帖子");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else {
            //发帖页面跳转
            Intent intent = new Intent(getActivity(), Post.class);
            startActivity(intent);
        }
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
    // 按钮点击事件处理
    public void toMy() {
        if (mListener != null) {
            mListener.onButtonClick();
        }
    }
}