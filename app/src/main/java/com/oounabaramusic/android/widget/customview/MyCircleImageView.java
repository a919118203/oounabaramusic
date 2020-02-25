package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.okhttputil.HttpUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCircleImageView extends CircleImageView {
    public static final int NO_NET=10000;
    public static final int LOAD_SUCCESS=10001;
    public static final int LOAD_FAILURE=10002;
    public static final int NO_COVER=10003;
    public static final int LOAD_END=10004;

    private Handler handler=new ImageHandler(this);
    private Bitmap defaultImage;
    private Handler eventHandler;
    private String url;
    private MyImage image;

    public MyCircleImageView(Context context) {
        super(context);
    }

    public MyCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImage(MyImage image){
        this.image=image;
        setImageBitmap(null);
        HttpUtil.loadImage(getContext(),image,handler);
    }

    public void refresh(){
        HttpUtil.loadImage(getContext(),image,handler);
    }

    public void setDefaultImage(Bitmap defaultImage) {
        this.defaultImage = defaultImage;
    }

    public void setEventHandler(Handler eventHandler) {
        this.eventHandler = eventHandler;
    }

    static class ImageHandler extends Handler{

        private MyCircleImageView iv;

        ImageHandler(MyCircleImageView iv){
            this.iv=iv;
        }

        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap=null;
            switch (msg.what){
                case NO_NET:
                    Toast.makeText(iv.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    iv.setImageBitmap(bitmap);
                    break;
                case LOAD_SUCCESS:
                    bitmap= (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap);
                    break;
                case NO_COVER:
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    iv.setImageBitmap(bitmap);
                    break;
                case LOAD_FAILURE:
                    Toast.makeText(iv.getContext(), "图片加载失败", Toast.LENGTH_SHORT).show();
                    if(iv.defaultImage==null){
                        bitmap=BitmapFactory.decodeResource(iv.getResources(), R.mipmap.default_image);
                    }else{
                        bitmap=iv.defaultImage;
                    }
                    iv.setImageBitmap(bitmap);
                    break;
            }


            if(iv.eventHandler!=null){
                Message msg1=new Message();
                msg1.what=LOAD_END;
                msg1.obj=bitmap;
                iv.eventHandler.sendMessage(msg1);
            }
        }
    }
}
