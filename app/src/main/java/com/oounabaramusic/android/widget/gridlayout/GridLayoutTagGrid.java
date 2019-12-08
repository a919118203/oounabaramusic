package com.oounabaramusic.android.widget.gridlayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.widget.textview.TextViewCell;

import java.util.List;

@SuppressLint("ViewConstructor")
public class GridLayoutTagGrid extends GridLayout {

    private Activity activity;
    public static final int COL_COUNT=5;          //列数
    public static final int ROW_HEIGHT=45;        //每行的高度（DP）

    public GridLayoutTagGrid(Activity activity) {
        super(activity);
        this.activity=  activity;
        init();
    }

    private void init() {
        GridLayout.LayoutParams lp=new GridLayout.LayoutParams();
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height= DensityUtil.dip2px(activity,ROW_HEIGHT*2);
        setRowCount(2);
        setColumnCount(COL_COUNT);
        int mar=DensityUtil.dip2px(activity,10);
        lp.setMargins(mar,mar,mar,mar);
        setLayoutParams(lp);
    }
}
