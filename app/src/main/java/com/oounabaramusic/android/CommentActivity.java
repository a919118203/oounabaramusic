package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.oounabaramusic.android.adapter.CommentAdapter;

public class CommentActivity extends BaseActivity {

    private CommentAdapter wonderfulCommentsAdapter;
    private CommentAdapter latestCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        RecyclerView wonderfulComments = findViewById(R.id.wonderful_comments);
        wonderfulComments.setAdapter(wonderfulCommentsAdapter=new CommentAdapter(this));
        wonderfulComments.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView latestComments = findViewById(R.id.latest_comments);
        latestComments.setAdapter(latestCommentsAdapter=new CommentAdapter(this));
        latestComments.setLayoutManager(new LinearLayoutManager(this));

        ImageView cover=findViewById(R.id.cover);
        cover.setFocusable(true);
        cover.setFocusableInTouchMode(true);
        cover.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
