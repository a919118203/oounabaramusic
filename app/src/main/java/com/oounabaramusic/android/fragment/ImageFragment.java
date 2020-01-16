package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageFragment extends BaseFragment {

    private int id;
    private View rootView;

    public ImageFragment(int id){
        this.id=id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_image,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        ImageView iv= (ImageView) view;
        iv.setImageDrawable(getActivity().getResources().getDrawable(id));
    }
}
