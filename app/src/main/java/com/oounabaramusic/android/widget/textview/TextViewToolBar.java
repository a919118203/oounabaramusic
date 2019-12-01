package com.oounabaramusic.android.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TextViewToolBar extends TextViewBigFont {
    public TextViewToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewToolBar(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setHeight(getWidth());
        super.onDraw(canvas);
    }
}
