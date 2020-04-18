package com.oounabaramusic.android.okhttputil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.MainActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.util.DigestUtils;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.VideoUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static final int NO_NET=-200;
    public static final int FAILURE=-300;
    private static final String lock="Mogeko";

    public static Map<String,Bitmap> bitmaps=new HashMap<>();

    /**
     * 登录
     * @param photo
     * @param activity
     */
    public static void login(String photo, final Activity activity){
        OkHttpClient client=new OkHttpClient();
        RequestBody body= new FormBody.Builder()
                .add("photo",photo)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath +"login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.printLog("HttpUtil/login error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();
                User user=gson.fromJson(response.body().string(),User.class);

                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(activity).edit();
                editor.putString("userId", String.valueOf(user.getId()));
                editor.putString("userName",user.getUserName());
                editor.putBoolean("login",true);
                editor.apply();

                Intent intent=new Intent(activity,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    /**
     * 本地音乐关联服务器音乐
     * @param context
     * @param md5s
     * @param handler
     */
    public static void checkIsServer(Context context,final List<String> md5s, final Handler handler){
        //如果没网就放弃
        if(!InternetUtil.checkNet(context)){
            handler.sendEmptyMessage(NO_NET);
            return;
        }

        final Gson gson=new Gson();

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",gson.toJson(md5s))
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"music/isServer")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //无论成没成功
                handler.sendEmptyMessage(LocalMusicActivity.MESSAGE_CHECK_IS_SERVER_END);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                //无论成没成功
                Message message=new Message();
                message.what=LocalMusicActivity.MESSAGE_CHECK_IS_SERVER_END;
                message.obj=response.body().string();
                handler.sendMessage(message);
            }
        });
    }


    public static void loadImage(final Context context, final MyImage image, final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (lock){
                    //如果没网，就加载默认图片,或者缓存
                    if(!InternetUtil.checkNet(context)){
                        handler.sendEmptyMessage(MyCircleImageView.NO_NET);
                        return;
                    }

                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("json",new Gson().toJson(image))
                            .build();

                    Request request = new Request.Builder()
                            .url(MyEnvironment.serverBasePath+"loadImage")
                            .post(body)
                            .build();

                    FileOutputStream fos = null;
                    InputStream is=null;

                    try {
                        Response response = client.newCall(request).execute();

                        String md5 = response.header("md5");

                        //如果已经缓存过了
                        if(bitmaps.containsKey(md5)){
                            Message msg = new Message();
                            msg.what=MyCircleImageView.LOAD_SUCCESS;
                            msg.obj=bitmaps.get(md5);
                            handler.sendMessage(msg);
                            return;
                        }

                        if(md5==null){
                            handler.sendEmptyMessage(MyCircleImageView.NO_COVER);
                            return;
                        }

                        is = Objects.requireNonNull(response.body()).byteStream();

                        File file = new File(MyEnvironment.cachePath+md5);
                        if(file.exists()){
                            is.skip(file.length());
                        }

                        fos = new FileOutputStream(file,true);

                        byte[] buff = new byte[1024];
                        int len;
                        while((len=is.read(buff))!=-1){
                            fos.write(buff,0,len);
                        }
                        fos.flush();

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                        //缓存
                        bitmaps.put(md5,bitmap);

                        //发送消息
                        Message msg = new Message();
                        msg.what=MyCircleImageView.LOAD_SUCCESS;
                        msg.obj=bitmap;
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(MyCircleImageView.LOAD_FAILURE);
                    }  finally {
                        try {
                            if(is!=null){
                                is.close();
                            }
                            if(fos!=null){
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 上传图片
     * @param context    上下文
     * @param image      包含contentType,contentId
     * @param filePath   图片文件路径
     * @param handler    handler
     */
    public static void uploadImage(final Context context, final MyImage image, final String filePath, final Handler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //如果没网
                if(!InternetUtil.checkNet(context)){
                    Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient client=new OkHttpClient();
                //图片文件
                File file = new File(filePath);
                //保存文件md5值
                image.setMd5(DigestUtils.md5HexOfFile(file));
                //构建RequestBody
                RequestBody body=new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file",file.getName(),
                                RequestBody.create(file, MediaType.parse("multipart/form-data")))
                        .build();
                //构建Request
                Request request = new Request.Builder()
                        .url(MyEnvironment.serverBasePath+"uploadImage")
                        .header("json", new Gson().toJson(image))
                        .post(body)
                        .build();
                //上传图片
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        handler.sendEmptyMessage(BasicCode.UPLOAD_FAILURE);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        handler.sendEmptyMessage(BasicCode.UPLOAD_IMAGE);
                    }
                });
            }
        }).start();
    }

    /**
     * 上传视频封面，使用第一帧作为视频封面
     * @param context            上下文
     * @param image              包含contentType,contentId
     * @param videoFilePath      视频文件路径
     * @param handler            handler
     */
    public static void uploadVideoCover(final Context context, final MyImage image, final String videoFilePath, final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!InternetUtil.checkNet(context)){
                    Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(HttpUtil.NO_NET);
                    return;
                }

                //如果视频文件不存在就退出
                File videoFile=new File(videoFilePath);
                if(!videoFile.exists())
                    return;

                Bitmap bitmap = VideoUtil.getVideoCover(videoFilePath);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                //将第一帧作为视频封面，将当前时间作为文件名，暂时创建文件
                final File file = new File(MyEnvironment.cachePath+
                        DigestUtils.md5HexOfString(FormatUtil.DateTimeToString(new Date())));
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(baos.toByteArray());
                    bos.flush();
                    bos.close();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap.recycle();

                uploadImage(context,image,file.getPath(),handler);
            }
        }).start();
    }

    static class StringCallBack implements Callback{

        Handler handler;
        int msgId;

        StringCallBack(Handler handler,int msgId){
            this.handler=handler;
            this.msgId=msgId;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            handler.sendEmptyMessage(FAILURE);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Message msg=new Message();
            msg.what=msgId;
            msg.obj=response.body().string();
            handler.sendMessage(msg);
        }
    }
}
