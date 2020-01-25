package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyCircleImageView extends CircleImageView {
    public static final int NO_NET=0;
    public static final int LOAD_SUCCESS=1;
    public static final int LOAD_FAILURE=2;

    public MyCircleImageView(Context context) {
        super(context);
    }

    public MyCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Handler handler=new MyImageView.ImageHandler(this);

    public void setImageUrl(String url){
        HttpUtil.loadImage(getContext(),url,new ImageHandler(this));
    }

    static class ImageHandler extends Handler{

        private ImageView iv;

        ImageHandler(ImageView iv){
            this.iv=iv;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NO_NET:
                    Toast.makeText(iv.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap=BitmapFactory.decodeResource(iv.getResources(),R.mipmap.default_image);
                    iv.setImageBitmap(bitmap);
                    break;
                case LOAD_SUCCESS:
                    Bitmap bitmap2= (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap2);
                    break;
                case LOAD_FAILURE:
                    Toast.makeText(iv.getContext(), "图片加载失败", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap3=BitmapFactory.decodeResource(iv.getResources(),R.mipmap.default_image);
                    iv.setImageBitmap(bitmap3);
                    break;
            }
        }
    }
}
