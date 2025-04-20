package com.example.yanyuan;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Picture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Picture extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ConstraintLayout body;
    ImageView Picture;

    public Picture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Picture.
     */
    // TODO: Rename and change types and number of parameters
    public static Picture newInstance(String param1, String param2) {
        Picture fragment = new Picture();
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
        View view =inflater.inflate(R.layout.fragment_picture, container, false);
        Picture = view.findViewById(R.id.picture);
        Bundle bundle = getArguments();
        String picture = bundle.getString("picture");
        String picture_type = bundle.getString("picture_type");

        //图片加载
        if (picture_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(picture, "drawable", getActivity().getPackageName());
                //设置图片,使用Glide加载图片
                Glide.with(this)
                        .load(resourceId)
                        .into(Picture);
        } else {
            //显示图片
            //创建一个新的图片控件
            ImageView imageView = new ImageView(getActivity());
            // 获取应用专属的图片目录
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // 构建图片的完整路径
            File imageFile = new File(storageDir, picture);

            // 检查文件是否存在
            if (!imageFile.exists()) {
                // 处理文件不存在的情况
                Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
            }

            //设置图片,使用Glide加载图片
            Glide.with(this)
                    .load(imageFile)
                    .into(Picture);
        }

        body = view.findViewById(R.id.body);
        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        return view;
    }

    public void removeSelf() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();
    }
}