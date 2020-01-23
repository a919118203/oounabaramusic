package com.oounabaramusic.android.okhttputil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;
import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.MainActivity;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    public static void checkIsServer(final List<String> md5s, final Handler handler){
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
                String[] jsonData=gson.fromJson(response.body().string(),String[].class);
                Map<String,Integer> result=new HashMap<>();
                for(int i=0;i<jsonData.length;i++){
                    result.put(md5s.get(i),Integer.valueOf(jsonData[i]));
                }

                //无论成没成功
                Message message=new Message();
                message.what=LocalMusicActivity.MESSAGE_CHECK_IS_SERVER_END;
                message.obj=result;
                handler.sendMessage(message);
            }
        });
    }
}
