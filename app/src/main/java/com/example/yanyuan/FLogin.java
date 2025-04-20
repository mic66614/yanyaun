package com.example.yanyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FLogin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText user_name,password;
    CardView login;
    MyOpenHelper helper;

    public FLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static FLogin newInstance(String param1, String param2) {
        FLogin fragment = new FLogin();
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
        View view = inflater.inflate(R.layout.fragment_f_login, container, false);

        user_name = view.findViewById(R.id.user_name);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        helper = new MyOpenHelper(getActivity());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user_name.getText().toString();
                String pass = password.getText().toString();
                if(username.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getActivity(), "用户名或密码为空", Toast.LENGTH_SHORT).show();
                }else {
                    SQLiteDatabase db = helper.getWritableDatabase();
                    String sql = "select * from user where user_name='"+username+"';";
                    Cursor cursor = db.rawQuery(sql,null);
                    if(cursor == null || cursor.getCount()==0){
                        Toast.makeText(getActivity(), "该用户不存在！", Toast.LENGTH_SHORT).show();
                    }else {
                        while (cursor.moveToNext()){
                            String Password = cursor.getString(2);
                            if(Password.equals(pass)) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ifLogin", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putInt("ifLogin",1).apply();
                                sharedPreferences.edit().putString("userName",username).apply();
                                sharedPreferences.edit().putString("password",pass).apply();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }else {
                                Toast.makeText(getActivity(), "账号或密码不正确!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    db.close();
                }
            }
        });
        return view;
    }
}