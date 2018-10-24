package com.example.jeong_woochang.fans.Adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.jeong_woochang.fans.R;

/**
 * Created by jeong-woochang on 2018. 8. 13..
 */

@SuppressLint("ValidFragment")
public class SumnailFragment extends Fragment {

    String sumnail;

    public SumnailFragment(String _sumnail) {
        sumnail=_sumnail;
    }

    public SumnailFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view=(LinearLayout)inflater.inflate(R.layout.fragment_sumnail,container,false);
        Log.d("SumnailFragment:::","");
        try{
            Glide.with(getContext())
                    .load("http://cfs7.tistory.com/upload_control/download.blog?fhandle=YmxvZzgyMzM1QGZzNy50aXN0b3J5LmNvbTovYXR0YWNoLzAvMDYwMDAwMDAwMDAwLmpwZw%3D%3D")
                    .into((ImageView)view.findViewById(R.id.thumbnail));
        }catch (Exception e){
            Log.e("Glide Execption:::",e.getMessage());
        }
        return view;
    }
}
