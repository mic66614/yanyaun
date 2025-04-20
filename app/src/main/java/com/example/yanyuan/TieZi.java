package com.example.yanyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TieZi#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TieZi extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView user_name,time,pindao,title,yulan_zhengwen,count_liulan,count_pinglun,count_dianzan;
    ImageView picture,user_tx;
    LinearLayout tiezi;

    MyOpenHelper helper;


    public TieZi() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tiezi.
     */
    // TODO: Rename and change types and number of parameters
    public static TieZi newInstance(String param1, String param2) {
        TieZi fragment = new TieZi();
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
        View view = inflater.inflate(R.layout.fragment_tie_zi, container, false);
        user_name = view.findViewById(R.id.user_name);
        pindao = view.findViewById(R.id.pindao);
        title = view.findViewById(R.id.title);
        yulan_zhengwen = view.findViewById(R.id.yulan_zhengwen);
        time = view.findViewById(R.id.time);
        count_liulan = view.findViewById(R.id.count_liulan);
        count_pinglun = view.findViewById(R.id.count_pinglun);
        count_dianzan = view.findViewById(R.id.count_dianzan);
        picture = view.findViewById(R.id.picture);
        tiezi = view.findViewById(R.id.tiezi);
        user_tx = view.findViewById(R.id.user_tx);

        //获取并设置帖子预览信息
        Bundle bundle = getArguments();
        int id = bundle.getInt("id");
        String User_name = bundle.getString("user_name");
        String Pindao = bundle.getString("pindao");
        String Title = bundle.getString("title");
        String Zhengwen = bundle.getString("zhengwen");
        String Time = bundle.getString("time");
        String img = bundle.getString("img");
        String img_type = bundle.getString("img_type");
        String picture_type = bundle.getString("picture_type");
        int Liulan = bundle.getInt("liulan");
        int Dianzan = bundle.getInt("dianzan");
        int Shoucang = bundle.getInt("shoucang");
        int Pinglun = bundle.getInt("pinglun");
        int flag = bundle.getInt("flag");
        String[] Pictures = bundle.getStringArray("pictures");

        helper = new MyOpenHelper(getContext());

        user_name.setText(User_name);
        pindao.setText("#"+Pindao);
        title.setText(Title);
        yulan_zhengwen.setText(Zhengwen);
        time.setText(Time);
        count_liulan.setText(Liulan+" 浏览");
        count_pinglun.setText(Pinglun+" 评论");
        count_dianzan.setText(Dianzan+" 点赞");
        //设置头像
        if(img.isEmpty()){
            //如果没有头像显示默认头像
            int resourceId = getResources().getIdentifier("head", "drawable", getActivity().getPackageName());
            user_tx.setImageResource(resourceId);
        }else {
            if (img_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(img, "drawable", getActivity().getPackageName());
                user_tx.setImageResource(resourceId);
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
                user_tx.setImageBitmap(bitmap);
            }
        }

        //用户头像点击事件
        user_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_name",User_name);
                Intent intent = new Intent(getActivity(), User2.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        // 获取图片的资源ID,第一张图片
        if(Pictures == null){ //如果帖子没有图片，则设置为默认图片
            int resourceId = getResources().getIdentifier("top", "drawable", getActivity().getPackageName());
        }else {
            if (picture_type.equals("drawable")){
                int resourceId = getResources().getIdentifier(Pictures[0], "drawable", getActivity().getPackageName());
                //设置图片,使用Glide加载图片
                Glide.with(this)
                        .load(resourceId)
                        .into(picture);
            }else {
                // 获取应用专属的图片目录
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                // 构建图片的完整路径
                File imageFile = new File(storageDir,Pictures[0]);

                // 检查文件是否存在
                if (!imageFile.exists()) {
                    // 处理文件不存在的情况
                    Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
                }

                // 解码图片文件为一个Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                //设置图片,使用Glide加载图片
                Glide.with(this)
                        .load(imageFile)
                        .into(picture);
            }
        }

        //帖子设置点击事件
        tiezi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入帖子详情页
                Intent intent = new Intent(getActivity(), TieZi2.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("id",id);
                bundle1.putString("img",img);
                bundle1.putString("img_type",img_type);
                bundle1.putString("time",Time);
                bundle1.putStringArray("pictures",Pictures);
                bundle1.putInt("flag",flag);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        //频道点击事件
        pindao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),YeMian.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("yemian",Pindao);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        return view;
    }

    public void load(){

    }
}