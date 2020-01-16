package com.oounabaramusic.android.widget.textview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.TagAdapter;
import com.oounabaramusic.android.widget.gridlayout.GridLayoutTagGrid;

@SuppressLint("AppCompatCustomView")
public class TextViewCell extends TextView {

    private Activity activity;
    private String name;
    private int row,col,position;
    private Drawable notSelect;
    private View.OnClickListener listener;
    private boolean selected=false;

    /**
     *
     * @param context         上下文
     * @param name            标签名
     * @param row             tableLayout中的位置
     * @param col             tableLayout中的位置
     * @param position        第position个tableLayout
     * @param listener
     */
    public  TextViewCell(Context context,String name,int row,int col,int position,View.OnClickListener listener){
        super(context);
        activity= (Activity) context;
        this.name=name;
        this.row=row;
        this.col=col;
        this.position=position;
        this.listener=listener;
        init();
    }

    private void init() {
        setText(name);
        setGravity(Gravity.CENTER);

        //判断这个单元格应该用哪个背景
        if(row==0) {
            if(col!= GridLayoutTagGrid.COL_COUNT-1){
                notSelect=activity.getDrawable(R.drawable.gridlayout_cell_right);
            }else{
                notSelect=activity.getDrawable(R.drawable.view_ripple_rectangle);
            }
        }else{
            if(col!=GridLayoutTagGrid.COL_COUNT-1){
                notSelect=activity.getDrawable(R.drawable.gridlayout_cell_top_right);
            }else{
                notSelect=activity.getDrawable(R.drawable.gridlayout_cell_top);;
            }
        }
        setBackground(notSelect);

        if(!name.equals("")){
            setOnClickListener(listener);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(text.length()!=0){
            setOnClickListener(listener);
        }else{
            setClickable(false);
        }
        if(selected){
            setBackground(activity.getDrawable(R.drawable.gridlayout_cell_selected));
        }else{
            setBackground(notSelect);
        }
        super.setText(text, type);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Drawable getNotSelect() {
        return notSelect;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextSize(12);
        super.onDraw(canvas);
    }
}
