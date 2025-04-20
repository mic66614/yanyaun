package com.example.yanyuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PinglunEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PinglunEdit extends Fragment {

    // 定义一个接口
    public interface OnFragmentInteractionListener {
        void onFragmentReplaceRequested();
    }
    private OnFragmentInteractionListener mListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText pinglun;
    LinearLayout background,bottom;
    ImageView send;
    MyOpenHelper helper;

    public PinglunEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PinglunEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static PinglunEdit newInstance(String param1, String param2) {
        PinglunEdit fragment = new PinglunEdit();
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
        View view = inflater.inflate(R.layout.fragment_pinglun_edit, container, false);

        //获取焦点
        pinglun = view.findViewById(R.id.pinglun);
        pinglun.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        pinglun.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //调用输入法
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        Bundle bundle = getArguments();
        int tid = bundle.getInt("tid");
        String user_name = bundle.getString("user_name");

        //获取当前登录的用户
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
        String Username = sharedPreferences.getString("userName",null);
        send = view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinglun.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "当前评论为空", Toast.LENGTH_SHORT).show();
                }else {
                    String Pinglun = pinglun.getText().toString();
                    helper = new MyOpenHelper(getActivity());
                    long timestamp = System.currentTimeMillis()/1000;
                    SQLiteDatabase db = helper.getWritableDatabase();
                    String sql = "insert into pinglun(tid,user_name,pinglun,time) values("+tid+",'"+Username+"','"+Pinglun+"',"+timestamp+");";
                    db.execSQL(sql);
                    sql = "update tiezi set pinglun = pinglun+1 where id = "+tid+";";
                    db.execSQL(sql);
                    removeSelf();
                    //提醒父activity替换评论区Fragment
                    requestFragmentReplace();
                }
            }
        });



        background = view.findViewById(R.id.background);
        bottom = view.findViewById(R.id.bottom);
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
        return view;
    }

    //Fragment移除
    public void removeSelf() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }

    // 调用此方法以通知Activity替换Fragment
    private void requestFragmentReplace() {
        if (mListener != null) {
            mListener.onFragmentReplaceRequested();
        }
    }
}