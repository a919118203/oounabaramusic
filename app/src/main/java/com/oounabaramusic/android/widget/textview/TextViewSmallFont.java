package com.oounabaramusic.android.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class TextViewSmallFont extends TextView {
    public TextViewSmallFont(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewSmallFont(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewSmallFont(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        super.onDraw(canvas);
    }
}
