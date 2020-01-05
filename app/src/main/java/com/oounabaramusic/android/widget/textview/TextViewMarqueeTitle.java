package com.oounabaramusic.android.widget.textview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TextViewMarqueeTitle extends TextViewBigFont {
    public TextViewMarqueeTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewMarqueeTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TextViewMarqueeTitle(Context context) {
        super(context);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine(true);
        setHorizontallyScrolling(true);
        setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
