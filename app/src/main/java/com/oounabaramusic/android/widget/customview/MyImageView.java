package com.oounabaramusic.android.widget.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
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

    private Handler handler=new ImageHandler(this);
    private Bitmap defaultImage;
    private Handler eventHandler;
    private String url;
    private boolean sizeAdaptive;
    private int datumWidth;

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

    public void setImageUrl(String url){
        this.url=url;
        HttpUtil.loadImage(getContext(),url,new ImageHandler(this));
    }

    /**
     * 是否根据图片大小自适应ImageView的大小
     * @param sizeAdaptive
     */
    public void setSizeAdaptive(boolean sizeAdaptive,int width) {
        this.sizeAdaptive = sizeAdaptive;
        datumWidth=width;
    }

    public void refresh(){
        HttpUtil.loadImage(getContext(),url,new ImageHandler(this));
    }

    public void setDefaultImage(Bitmap defaultImage) {
        this.defaultImage = defaultImage;
    }

    public void setEventHandler(Handler eventHandler) {
        this.eventHandler = eventHandler;
    }

    static class ImageHandler extends Handler{

        private MyImageView iv;

        ImageHandler(MyImageView iv){
            this.iv=iv;
        }

        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap=null;
            switch (msg.what){
                case MyCircleImageView.NO_NET:
                    Toast.makeText(iv.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    break;
                case MyCircleImageView.LOAD_SUCCESS:
                    bitmap= (Bitmap) msg.obj;
                    break;
                case MyCircleImageView.NO_COVER:
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    break;
                case MyCircleImageView.LOAD_FAILURE:
                    Toast.makeText(iv.getContext(), "图片加载失败", Toast.LENGTH_SHORT).show();
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    break;
            }
            if(bitmap!=null&&iv.sizeAdaptive&&iv.datumWidth!=0){
                int height=bitmap.getHeight();
                int width=bitmap.getWidth();
                ViewGroup.LayoutParams lp = iv.getLayoutParams();
                lp.width=iv.datumWidth;
                lp.height=(iv.datumWidth*height)/width;
                iv.requestLayout();
                iv.setImageBitmap(bitmap);
            }

            iv.setImageBitmap(bitmap);

            if(iv.eventHandler!=null){
                Message msg1=new Message();
                msg1.what=MyCircleImageView.LOAD_END;
                msg1.obj=bitmap;
                iv.eventHandler.sendMessage(msg1);
            }
        }
    }
}
