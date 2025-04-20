package com.example.yanyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView user_img;
    TextView user_name,qianming,guanzhu;
    MyOpenHelper helper;

    public User() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user.
     */
    // TODO: Rename and change types and number of parameters
    public static User newInstance(String param1, String param2) {
        User fragment = new User();
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
        View view =inflater.inflate(R.layout.fragment_user, container, false);
        Bundle bundle = getArguments();
        String User_name = bundle.getString("user_name");
        String Qianming = bundle.getString("qianming");
        String img = bundle.getString("img");
        String img_type = bundle.getString("img_type");

        helper = new MyOpenHelper(getActivity());
        user_img = view.findViewById(R.id.user_img);
        user_name = view.findViewById(R.id.user_name);
        qianming = view.findViewById(R.id.qianming);
        guanzhu = view.findViewById(R.id.guanzhu);
        //设置头像
        if(img.isEmpty()){
            //如果没有头像显示默认头像
            int resourceId = getResources().getIdentifier("head", "drawable", getActivity().getPackageName());
            user_img.setImageResource(resourceId);
        }else {
            if (img_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(img, "drawable", getActivity().getPackageName());
                user_img.setImageResource(resourceId);
            }else {
                // 获取应用专属的图片目录
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                // 构建图片的完整路径
                File imageFile = new File(storageDir,img);

                // 检查文件是否存在
                if (!imageFile.exists()) {
                    // 处理文件不存在的情况
                    Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
                }

                // 解码图片文件为一个Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                //显示图片
                user_img.setImageBitmap(bitmap);
            }
        }
        user_name.setText(User_name);
        //用户头像点击事件
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_name",User_name);
                Intent intent = new Intent(getActivity(), User2.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        //设置签名
        if(Qianming.isEmpty()){
            qianming.setText("这个人很懒，还没有签名");
        }else {
            qianming.setText(Qianming);
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        //获取关注数据
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);
        String sql = "select * from guanzhu where user = '"+Username+"' and b_user = '"+User_name+"'";
        Cursor cursor3 = db.rawQuery(sql,null);
        if(cursor3.getCount() == 0){
            guanzhu.setText("关注");
        }else {
            guanzhu.setText("已关注");
        }

        //设置关注点击事件
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String flag = guanzhu.getText().toString();
                if(flag.equals("已关注")){
                    //当前是关注状态
                    String sql = "delete from guanzhu where user = '"+Username+"' and b_user = '"+User_name+"'";
                    db.execSQL(sql);
                    sql = "update user set fensi = fensi - 1 where user_name = '"+User_name+"'";
                    db.execSQL(sql);
                    sql = "update user set guanzhu = guanzhu - 1 where user_name = '"+Username+"'";
                    db.execSQL(sql);
                    guanzhu.setText("关注");
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
                db.close();
            }
        });

        db.close();
        return view;
    }
}