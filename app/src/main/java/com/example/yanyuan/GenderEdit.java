package com.example.yanyuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenderEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenderEdit extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout background,bottom;
    TextView unknown,man,woman;
    CardView cancel;
    MyOpenHelper helper;
    String sql;


    // 定义一个接口
    public interface OnButtonClickListener {
        void genderEdit();
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // 当Fragment不再存在时，避免内存泄漏
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public GenderEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenderEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static GenderEdit newInstance(String param1, String param2) {
        GenderEdit fragment = new GenderEdit();
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
        View view = inflater.inflate(R.layout.fragment_gender_edit, container, false);

        background = view.findViewById(R.id.background);
        bottom = view.findViewById(R.id.bottom);
        unknown = view.findViewById(R.id.unknown);
        man = view.findViewById(R.id.man);
        woman = view.findViewById(R.id.woman);
        cancel = view.findViewById(R.id.cancel);


        unknown.setOnClickListener(this);
        man.setOnClickListener(this);
        woman.setOnClickListener(this);
        cancel.setOnClickListener(this);


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

    @Override
    public void onClick(View v) {
        helper = new MyOpenHelper(getActivity());
        if (v.getId() == R.id.cancel){
            removeSelf();
        }else {
            //获取当前登录的用户
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
            String Username = sharedPreferences.getString("userName",null);

            SQLiteDatabase db = helper.getWritableDatabase();

            TextView textView = getView().findViewById(v.getId());
            String newGender = textView.getText().toString();

            sql = "update user set gender = '"+newGender+"' where user_name = '"+Username+"'";
            db.execSQL(sql);

            Toast.makeText(getActivity(), "性别修改成功", Toast.LENGTH_SHORT).show();
            db.close();

            toMy();
            removeSelf();
        }
    }

    public void removeSelf() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }

    public void toMy() {
        if (mListener != null) {
            mListener.genderEdit();
        }
    }
}