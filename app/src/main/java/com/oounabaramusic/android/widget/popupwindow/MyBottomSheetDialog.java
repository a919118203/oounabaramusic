package com.oounabaramusic.android.widget.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;

public class MyBottomSheetDialog extends BottomSheetDialog {
    public MyBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
    }
}
