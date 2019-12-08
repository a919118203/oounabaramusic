package com.oounabaramusic.android.widget.textview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.TagAdapter;
import com.oounabaramusic.android.widget.gridlayout.GridLayoutTagGrid;

public class TextViewCell extends TextViewSmallFont implements View.OnClickListener{

    private Activity activity;
    private String name;
    private int row,col,position;
    private Drawable notSelect;
    public  TextViewCell(Context context,String name,int row,int col,int position){
        super(context);
        activity= (Activity) context;
        this.name=name;
        this.row=row;
        this.col=col;
        this.position=position;
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

        if(TagAdapter.isSelected(position,row,col)){
            setBackground(activity.getDrawable(R.drawable.gridlayout_cell_selected));
        }else{
            setBackground(notSelect);
        }

        if(!name.equals("")){
            setOnClickListener(this);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(text.length()!=0){
            setOnClickListener(this);
        }else{
            setClickable(false);
        }
        if(TagAdapter.isSelected(position,row,col)){
            setBackground(activity.getDrawable(R.drawable.gridlayout_cell_selected));
        }else{
            setBackground(notSelect);
        }
        super.setText(text, type);
    }

    @Override
    public void onClick(View v) {
        switch (TagAdapter.selectCell(position,row,col)){
            case TagAdapter.SELECT:
                setBackground(activity.getDrawable(R.drawable.gridlayout_cell_selected));
                break;
            case TagAdapter.INVERT_SELECTION:
                setBackground(notSelect);
                break;
            case TagAdapter.SELECT_FAIL:
                Toast.makeText(activity,"最多只可以3个标签",Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
