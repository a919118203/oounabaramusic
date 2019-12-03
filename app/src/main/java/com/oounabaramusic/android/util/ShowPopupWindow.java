package com.oounabaramusic.android.util;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.oounabaramusic.android.R;

public class ShowPopupWindow{

    private FrameLayout rootView;
    private PopupWindow pw;
    private View block;

    public ShowPopupWindow(View contentView,FrameLayout rootView){
        this.rootView=rootView;
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

    private void initBlockView() {
        block =new View(rootView.getContext());
        FrameLayout.LayoutParams pl=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        block.setLayoutParams(pl);
        block.setBackgroundColor(rootView.getResources().getColor(R.color.font_negative));
        block.setAlpha(0.5f);
        block.setVisibility(View.GONE);
        rootView.addView(block);
    }

    public void showPopupMenu() {
        block.setVisibility(View.VISIBLE);
        pw.showAtLocation(rootView, Gravity.BOTTOM,0,0);
    }
}
