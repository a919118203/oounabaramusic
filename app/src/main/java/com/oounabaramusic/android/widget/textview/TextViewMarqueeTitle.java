package com.oounabaramusic.android.widget.textview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TextViewMarqueeTitle extends TextViewBigFont {
    public TextViewMarqueeTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewMarqueeTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewMarqueeTitle(Context context) {
        super(context);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
