package com.oounabaramusic.android.widget.customview;

import android.annotation.SuppressLint;
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
import com.oounabaramusic.android.util.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@SuppressLint("AppCompatCustomView")
public class MyImageView extends ImageView {

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Handler handler=new ImageHandler(this);

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
                case MyCircleImageView.NO_NET:
                    Toast.makeText(iv.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    iv.setImageBitmap(bitmap);
                    break;
                case MyCircleImageView.LOAD_SUCCESS:
                    Bitmap bitmap2= (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap2);
                    break;
                case MyCircleImageView.LOAD_FAILURE:
                    Toast.makeText(iv.getContext(), "图片加载失败", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap3=BitmapFactory.decodeResource(iv.getResources(),R.mipmap.default_image);
                    iv.setImageBitmap(bitmap3);
                    break;
            }
        }
    }
}
