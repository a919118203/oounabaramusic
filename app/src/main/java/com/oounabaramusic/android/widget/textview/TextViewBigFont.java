package com.oounabaramusic.android.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewDebug;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class TextViewBigFont extends TextView {
    public TextViewBigFont(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewBigFont(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewBigFont(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        super.onDraw(canvas);
    }
}
