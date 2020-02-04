package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestFragment extends BaseFragment implements View.OnClickListener{

    private MyCollectionActivity activity;
    private View rootView;

    private List<String> list;
    public TestFragment(MyCollectionActivity activity){
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

    private void init(View rootView) {

        list=new ArrayList<>();

        for(int i=0;i<30;i++){
            list.add("1506");
        }
        rootView.findViewById(R.id.sss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(0);
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder>{


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(activity).inflate(
                    R.layout.rv_item_tag_cell,
                    (ViewGroup) activity.getWindow().getDecorView(),
                    false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
