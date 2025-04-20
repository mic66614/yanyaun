package com.example.yanyuan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link My#newInstance} factory method to
 * create an instance of this fragment.
 */
public class My extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView bianji,user_img;
    TextView user_name,user_qm,count_fensi,count_guanzhu,count_jinbi;
    LinearLayout fensi,guanzhu,jinbi;
    CardView wdtz,lljl,wdsc,exit;
    MyOpenHelper helper;

    public My() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment My.
     */
    // TODO: Rename and change types and number of parameters
    public static My newInstance(String param1, String param2) {
        My fragment = new My();
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
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        user_img = view.findViewById(R.id.user_img);
        user_name = view.findViewById(R.id.user_name);
        user_qm = view.findViewById(R.id.user_qm);
        count_fensi = view.findViewById(R.id.count_fensi);
        count_guanzhu = view.findViewById(R.id.count_guanzhu);
        count_jinbi = view.findViewById(R.id.count_jinbi);
        //设置用户信息
        setUser();

        bianji = view.findViewById(R.id.bianji);
        bianji.setOnClickListener(this);

        fensi = view.findViewById(R.id.fensi);
        fensi.setOnClickListener(this);

        guanzhu = view.findViewById(R.id.guanzhu);
        guanzhu.setOnClickListener(this);

        jinbi = view.findViewById(R.id.jinbi);


        wdtz = view.findViewById(R.id.wdtz);
        wdtz.setOnClickListener(this);

        lljl = view.findViewById(R.id.lljl);
        lljl.setOnClickListener(this);

        wdsc = view.findViewById(R.id.wdsc);
        wdsc.setOnClickListener(this);

        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUser();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v.getId() == R.id.bianji){
            //编辑页面跳转
            Intent intent = new Intent(getActivity(), UserEdit.class);
            startActivity(intent);
        } else if (v.getId() == R.id.fensi) {
            //粉丝页面跳转
            bundle.putString("yemian","我的粉丝");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.guanzhu) {
            //关注页面跳转
            bundle.putString("yemian","我的关注");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.wdtz) {
            //我的帖子页面跳转
            bundle.putString("yemian","我的帖子");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.lljl) {
            //浏览记录页面跳转
            bundle.putString("yemian","浏览记录");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.wdsc) {
            //我的收藏页面跳转
            bundle.putString("yemian","我的收藏");
            Intent intent = new Intent(getActivity(), YeMian.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            //退出按钮
            AlertDialog.Builder ald = new AlertDialog.Builder(getActivity());
            ald.setTitle("退出登录");
            ald.setMessage("确定要退出登录吗?");
            ald.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("ifLogin",0).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            ald.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            ald.show();
        }
    }
    public void setUser(){
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin",Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);
        user_name.setText(Username);

        helper = new MyOpenHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        //从数据库获取登录用户的信息
        String sql = "select * from user where user_name='"+Username+"';";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        String qianming = cursor.getString(4);
        String img = cursor.getString(5);
        int c_jinbi = cursor.getInt(6);
        int c_guanzhu = cursor.getInt(7);
        int c_fensi = cursor.getInt(8);
        String img_type = cursor.getString(9);

        //设置签名
        if(qianming.isEmpty()){
            user_qm.setText("还没有设置签名哦！");
        }else {
            user_qm.setText(qianming);
        }

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


        //设置粉丝、关注、金币数量
        count_fensi.setText(String.valueOf(c_fensi));
        count_guanzhu.setText(String.valueOf(c_guanzhu));
        count_jinbi.setText(String.valueOf(c_jinbi));

        db.close();
    }
}