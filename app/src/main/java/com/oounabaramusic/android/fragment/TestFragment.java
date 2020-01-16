package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.anim.TextSizeChangeAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private TextSizeChangeAnimation animation;

    public TestFragment(Activity activity){
        this.activity=activity;
        setTitle("测试用");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_test,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        TextView tv=view.findViewById(R.id.text);
        animation=new TextSizeChangeAnimation(activity,tv);

        view.findViewById(R.id.to_big).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.toBig();
            }
        });

        view.findViewById(R.id.to_small).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.toSmall();
            }
        });

    }


}
