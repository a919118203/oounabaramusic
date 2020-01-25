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
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.util.DigestUtils;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

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

    public static void checkIsServer(Context context,final List<String> md5s, final Handler handler){
        //如果没网，就加载默认图片
        if(!InternetUtil.checkNet(context)){
            handler.sendEmptyMessage(LocalMusicActivity.MESSAGE_NO_NET);
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

    public static void loadImage(Context context, String url, final Handler handler){
        String a=url.substring(url.lastIndexOf("?"));
        String[] b=a.split("=");
        String filePath= MyEnvironment.cachePath+DigestUtils.md5HexOfString(b[0])+b[1];
        final File file=new File(filePath);
        if(file.exists()){
            LogUtil.printLog("加载图片：本地有缓存");
            Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());
            Message message=new Message();
            message.what=MyCircleImageView.LOAD_SUCCESS;
            message.obj=bitmap;
            handler.sendMessage(message);
            return;
        }

        LogUtil.printLog("加载图片：服务器音乐封面，本地缓存已被删除，请求服务器获取封面");
        LogUtil.printLog("Url: "+url);
        //如果没网，就加载默认图片
        if(!InternetUtil.checkNet(context)){
            handler.sendEmptyMessage(MyCircleImageView.NO_NET);
        }

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(MyCircleImageView.LOAD_FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is=response.body().byteStream();

                file.createNewFile();
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
                byte[] buff=new byte[1024];
                int len;
                while((len=is.read(buff))!=-1){
                    bos.write(buff,0,len);
                }
                bos.flush();
                bos.close();

                Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());

                Message message=new Message();
                message.what=MyCircleImageView.LOAD_SUCCESS;
                message.obj=bitmap;

                handler.sendMessage(message);
            }
        });
    }
}