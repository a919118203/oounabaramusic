package com.oounabaramusic.android.util;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.oounabaramusic.android.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ShowPopupWindow{

    private ViewGroup rootView;
    private View contentView;
    private PopupWindow pw;
    private View block;

    public ShowPopupWindow(View contentView,FrameLayout rootView){
        this.rootView=rootView;
        this.contentView=contentView;
        pw=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setFocusable(true);
        pw.setTouchable(true);
        initBlockView();
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(block !=null){
                    block.setVisibility(View.GONE);
                }
            }
        });
    }

    public ShowPopupWindow(View contentView, CoordinatorLayout rootView){
        this.rootView=rootView;
        this.contentView=contentView;
        pw=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setFocusable(true);
        pw.setTouchable(true);
        initBlockView();
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(block !=null){
                    block.setVisibility(View.GONE);
                }
            }
        });
    }

    public ShowPopupWindow(View contentView,FrameLayout rootView,int width,int height){
        this.rootView=rootView;
        this.contentView=contentView;
        pw=new PopupWindow(contentView, width, height);
        pw.setFocusable(true);
        pw.setTouchable(true);
        initBlockView();
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(block !=null){
                    block.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initBlockView() {
        block =new View(rootView.getContext());
        FrameLayout.LayoutParams pl=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        block.setLayoutParams(pl);
        block.setBackgroundColor(rootView.getResources().getColor(R.color.positive));
        block.setAlpha(0.5f);
        block.setVisibility(View.GONE);
        rootView.addView(block);
    }

    public void showPopupMenu() {
        block.setVisibility(View.VISIBLE);
        pw.showAtLocation(rootView, Gravity.BOTTOM,0,0);
    }

    public void showPopupMenu(int gravity) {
        block.setVisibility(View.VISIBLE);
        pw.showAtLocation(rootView,gravity,0,0);
    }

    public void showPopupMenu(int gravity,int x,int y) {
        block.setVisibility(View.VISIBLE);
        pw.showAtLocation(rootView, gravity,x,y);
    }

    public void dismiss(){
        pw.dismiss();
    }

    public View getContentView() {
        return contentView;
    }
}
