package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.CommentAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PostCommentFragment extends Fragment {

    private Activity activity;
    private View rootView;
    private CommentAdapter adapter;
    private View reSetFocus;

    public PostCommentFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            View view=LayoutInflater.from(activity).inflate(R.layout.fragment_post_comment,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
//        RecyclerView recyclerView=view.findViewById(R.id.latest_comments);
//        recyclerView.setAdapter(adapter=new CommentAdapter(activity));
//        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        reSetFocus=view.findViewById(R.id.re);
        reSetFocus.setFocusable(true);
        reSetFocus.setFocusableInTouchMode(true);
        reSetFocus();
    }

    public void reSetFocus(){
        reSetFocus.requestFocus();
    }
}
