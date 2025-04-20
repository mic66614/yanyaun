package com.example.yanyuan;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardPindao#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardPindao extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView pindao_name;
    ImageView pindao_img;
    CardView pindao;


    public CardPindao() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardPindao.
     */
    // TODO: Rename and change types and number of parameters
    public static CardPindao newInstance(String param1, String param2) {
        CardPindao fragment = new CardPindao();
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
        View view = inflater.inflate(R.layout.fragment_card_pindao, container, false);

        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        int resourceId = bundle.getInt("resourceId");

        pindao_img = view.findViewById(R.id.pindao_img);
        pindao_name = view.findViewById(R.id.pindao_name);

        pindao_img.setImageResource(resourceId);
        pindao_name.setText(name);

        //点击进入相应频道
        pindao = view.findViewById(R.id.pindao);
        pindao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("yemian",name);
                Intent intent = new Intent(getActivity(),YeMian.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        return view;
    }

    public void setPindao(){

    }
}