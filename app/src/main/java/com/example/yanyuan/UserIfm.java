package com.example.yanyuan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserIfm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserIfm extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MyOpenHelper helper;
    ImageView user_img;
    TextView id,user_name,user_qm,user_gender;
    CardView name,qm,gender,save;
    // PICK_IMAGE_REQUEST是一个int常量，用于在onActivityResult中识别这个请求
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.name){
            toMy("user_name");
        } else if (v.getId() == R.id.qm) {
            toMy("user_qm");
        }else if (v.getId() == R.id.gender){
            toMy("gender");
        }else {
            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        }
    }
    // 定义一个接口
    public interface OnButtonClickListener {
        void onButtonClick(String type);
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

    public UserIfm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserIfm.
     */
    // TODO: Rename and change types and number of parameters
    public static UserIfm newInstance(String param1, String param2) {
        UserIfm fragment = new UserIfm();
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
        View view = inflater.inflate(R.layout.fragment_user_ifm, container, false);
        user_img = view.findViewById(R.id.user_img);
        id = view.findViewById(R.id.id);
        user_name = view.findViewById(R.id.user_name);
        user_gender = view.findViewById(R.id.user_gender);
        load();

        name = view.findViewById(R.id.name);
        name.setOnClickListener(this);

        qm = view.findViewById(R.id.qm);
        qm.setOnClickListener(this);

        gender = view.findViewById(R.id.gender);
        gender.setOnClickListener(this);

        save = view.findViewById(R.id.save);
        save.setOnClickListener(this);


        //用户选择图片功能实现
        user_img.setOnClickListener(new View.OnClickListener() {
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
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
       load();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                // 从Uri获取图片
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // 生成当前时间（精确到秒）的文件名
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "image_" + timeStamp + ".jpg";

                // 指定保存图片的目录（这里使用内部存储）
                // 获取应用专属的图片目录
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = new File(storageDir, imageFileName);

                // 保存图片
                OutputStream os = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                //获取当前登录的用户
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
                String Username = sharedPreferences.getString("userName",null);

                String sql = "update user set img = '"+imageFileName+"',img_type = 'picture' where user_name = '"+Username+"'";
                helper = new MyOpenHelper(getActivity());
                SQLiteDatabase db = helper.getWritableDatabase();
                db.execSQL(sql);
                db.close();
                Toast.makeText(getActivity(), "图片上传成功", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "图片上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //加载页面数据
    public void load(){
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        helper = new MyOpenHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from user where user_name = '"+Username+"'";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        String img = cursor.getString(5);
        String img_type = cursor.getString(9);
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

        //设置id
        int Id = cursor.getInt(0);
        id.setText(""+Id);

        //设置用户名
        String User_name = cursor.getString(1);
        user_name.setText(User_name);

        //设置性别
        String Gender = cursor.getString(3);
        if (Gender.isEmpty()){
            user_gender.setText("未知");
        }else {
            user_gender.setText(Gender);
        }

        db.close();
    }

    // 按钮点击事件处理
    public void toMy(String type) {
        if (mListener != null) {
            mListener.onButtonClick(type);
        }
    }
}