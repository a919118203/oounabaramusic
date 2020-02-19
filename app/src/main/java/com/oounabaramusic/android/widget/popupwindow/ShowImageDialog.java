package com.oounabaramusic.android.widget.popupwindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

public class ShowImageDialog {

    private MyImage image;
    private Activity activity;
    private AlertDialog dialog;

    private MyImageView imageView;

    public ShowImageDialog(Activity activity, MyImage image){
        this.activity=activity;
        this.image=image;

        init();
    }

    private void init(){
        imageView = new MyImageView(activity);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        dialog=new AlertDialog.Builder(activity)
                .setView(imageView)
                .create();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void show(){
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width=activity.getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        //必须设置背景，不然会有默认的padding效果
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageView.setSizeAdaptive(true,activity.getResources().getDisplayMetrics().widthPixels);
        imageView.setImage(image);

    }
}
