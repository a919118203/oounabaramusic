package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.CommentAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PostCommentFragment extends BaseFragment {

    private PostActivity activity;
    private View rootView;
    private CommentAdapter adapter;
    private View reSetFocus;

    private EditText commentContent;

    public PostCommentFragment(PostActivity activity,EditText commentContent){
        this.commentContent=commentContent;
        this.activity=activity;
        setTitle("评论");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_post_comment,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initContent();
    }

    public CommentAdapter getAdapter() {
        return adapter;
    }

    private void init() {
        RecyclerView recyclerView=rootView.findViewById(R.id.latest_comments);
        recyclerView.setAdapter(adapter=new CommentAdapter(activity,commentContent));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        reSetFocus=rootView.findViewById(R.id.re);
        reSetFocus.setFocusable(true);
        reSetFocus.setFocusableInTouchMode(true);
        reSetFocus();
    }

    public void initContent() {
        int userId= SharedPreferencesUtil.getUserId(activity.sp);
        adapter.cancelSelect();

        Comment comment=new Comment();
        comment.setMainUserId(userId);
        comment.setTargetType(Comment.POST);
        comment.setTargetId(activity.getPostId());
        comment.setOrderByType(Comment.ORDER_BY_DATE);


        new S2SHttpUtil(
                activity,
                new Gson().toJson(comment),
                MyEnvironment.serverBasePath+"getComment",new MyHandler(this))
                .call(BasicCode.GET_COMMENT_END);
    }

    public void reSetFocus(){
        reSetFocus.requestFocus();
    }

    static class MyHandler extends Handler{
        PostCommentFragment fragment;
        MyHandler(PostCommentFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_COMMENT_END:
                    List<Comment> dataList=new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<Comment>>(){}.getType());
                    fragment.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
