package com.example.yanyuan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PinDao#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PinDao extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PinDao() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PinDao.
     */
    // TODO: Rename and change types and number of parameters
    public static PinDao newInstance(String param1, String param2) {
        PinDao fragment = new PinDao();
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
        View view = inflater.inflate(R.layout.fragment_pin_dao, container, false);

        getPinDao();

        return view;
    }

    public void getPinDao(){
        MyOpenHelper helper = new MyOpenHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from pindao";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String name = cursor.getString(1);
            String img = cursor.getString(2);
            int resourceId = getResources().getIdentifier(img, "drawable", getActivity().getPackageName());

            CardPindao cardPindao = new CardPindao();

            Bundle bundle = new Bundle();
            bundle.putString("name",name);
            bundle.putInt("resourceId",resourceId);

            cardPindao.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment,cardPindao);
            transaction.commit();
        }
        db.close();
    }
}