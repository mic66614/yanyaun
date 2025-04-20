package com.example.yanyuan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserIfmEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserIfmEdit extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ScrollView background;
    LinearLayout bottom;
    EditText user_ifm;
    MyOpenHelper helper;
    CardView save;
    String sql;

    // 定义一个接口
    public interface OnButtonClickListener {
        void userIfmEdit();
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


    public UserIfmEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserNameEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static UserIfmEdit newInstance(String param1, String param2) {
        UserIfmEdit fragment = new UserIfmEdit();
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
        View view = inflater.inflate(R.layout.fragment_user_ifm_edit, container, false);

        background = view.findViewById(R.id.background);
        bottom = view.findViewById(R.id.bottom);
        user_ifm = view.findViewById(R.id.user_ifm);
        helper = new MyOpenHelper(getContext());

        SQLiteDatabase db = helper.getWritableDatabase();
        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);

        //获取焦点
        user_ifm.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        user_ifm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //调用输入法
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        if(type.equals("user_name")){
            user_ifm.setHint("请填写用户名");
            user_ifm.setText(Username);
        }else {
            user_ifm.setHint("请填写个性签名");
            sql = "select qianming from user where user_name = '"+Username+"'";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToNext();
            String qm = cursor.getString(0);
            if(qm.isEmpty()){

            }else {
                user_ifm.setText(qm);
            }
        }



        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取底部点击区域
                Rect rect = new Rect();
                bottom.getHitRect(rect);
                // 检查点击事件是否在底部的区域内
                if (rect.contains((int) event.getX(), (int) event.getY())) {
                } else {
                    // 点击事件发生在页面的其他区域，执行事件
                    //移除这个Fragment
                    removeSelf();
                }
                return true; // 返回true表示事件已被消费
            }
        });


        //保存按钮点击事件
        save = view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                if (type.equals("user_name")){
                    String newUserName = user_ifm.getText().toString();
                    if (newUserName.isEmpty()){
                        Toast.makeText(getActivity(), "用户名为空", Toast.LENGTH_SHORT).show();
                    } else if (newUserName.equals(Username)) {
                        //新用户名没有变化，不作出反应
                        toMy();
                        removeSelf();
                    } else {
                        sql = "select * from user where user_name='"+newUserName+"';";
                        Cursor cursor = db.rawQuery(sql,null);
                        if (cursor.getCount() == 0){
                            sql = "update user set user_name = '"+newUserName+"' where user_name = '"+Username+"'";
                            db.execSQL(sql);

                            //修改成功后让用户重新登录
                            AlertDialog.Builder ald = new AlertDialog.Builder(getActivity());
                            ald.setTitle("修改成功");
                            ald.setMessage("用户名修改成功，请重新登录。");
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
                            ald.show();
                        }else {
                            //当前用户名已被注册
                            Toast.makeText(getActivity(), "当前用户名已存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    String newUserQm = user_ifm.getText().toString();
                    if (newUserQm.isEmpty()){
                        Toast.makeText(getActivity(), "什么签名都没有输入哦", Toast.LENGTH_SHORT).show();
                    } else if (newUserQm.length() >20) {
                        Toast.makeText(getActivity(), "最多输入20个字哦", Toast.LENGTH_SHORT).show();
                    }else {
                        sql = "update user set qianming = '"+newUserQm+"' where user_name = '"+Username+"'";
                        db.execSQL(sql);
                        Toast.makeText(getActivity(), "签名修改成功", Toast.LENGTH_SHORT).show();
                        toMy();
                        removeSelf();
                    }

                }
                db.close();
            }
        });

        db.close();
        return view;
    }

    //Fragment移除
    public void removeSelf() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }

    // 按钮点击事件处理
    public void toMy() {
        if (mListener != null) {
            mListener.userIfmEdit();
        }
    }
}