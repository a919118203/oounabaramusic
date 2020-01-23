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
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.printLog("未知错误");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.body()==null){
                    handler.sendEmptyMessage(-1);
                    return;
                }
                InputStream is=response.body().byteStream();
                Bitmap bitmap= BitmapFactory.decodeStream(is);

                Message message=new Message();
                message.what=1;
                message.obj=bitmap;

                handler.sendMessage(message);
            }
        });
    }

    static class ImageHandler extends Handler{

        private ImageView iv;

        ImageHandler(ImageView iv){
            this.iv=iv;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    Toast.makeText(iv.getContext(), "发生错误，稍后重试", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Bitmap bitmap= (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap);
                    break;
            }
        }
    }
}
