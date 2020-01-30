package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.SingerClassificationActivity;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingerClassificationHttpUtil {

    /**
     * 获取歌手的类型标签
     * @param context
     * @param handler
     */
    public static void loadSingerType(Context context, final Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"music/loadSingerType")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();
                String json=response.body().string();
                Message msg=new Message();
                msg.what= SingerClassificationActivity.MESSAGE_TYPE_LOAD_END;
                msg.obj=json;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 根据歌手类型获取歌手，并且获取用户的关注列表，用来判断用户有没有关注
     * @param context   上下文
     * @param country   国家
     * @param type      类型
     * @param userId    用户id
     * @param handler
     */
    public static void loadSinger(Context context,int country,int type,String userId,final Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("country",country+"")
                .add("type",type+"")
                .add("userId",userId)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"music/loadSinger")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                Message msg=new Message();
                msg.what=SingerClassificationActivity.MESSAGE_LOAD_SINGER_END;
                msg.obj=response.body().string();

                handler.sendMessage(msg);
            }
        });
    }

    public static void toFollowSinger(Context context,String userId,int singerId,final Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("userId",userId)
                .add("singerId",singerId+"")
                .build();

        Request request=new Request.Builder()
                .post(body)
                .url(MyEnvironment.serverBasePath+"toFollowSinger")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message message=new Message();
                message.what=SingerClassificationActivity.MESSAGE_FOLLOW_SINGER_END;
                message.obj=response.body().string();
                handler.sendMessage(message);
            }
        });
    }


    public static void cancelFollowSinger(Context context,String userId,int singerId,final Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("userId",userId)
                .add("singerId",singerId+"")
                .build();

        Request request=new Request.Builder()
                .post(body)
                .url(MyEnvironment.serverBasePath+"cancelFollowSinger")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message message=new Message();
                message.what=SingerClassificationActivity.MESSAGE_CANCEL_FOLLOW_END;
                message.obj=response.body().string();
                handler.sendMessage(message);
            }
        });
    }

    public static void loadSingerBySingerId(Context context,String singerId,String userId,final Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("singerId",singerId)
                .add("userId",userId)
                .build();

        Request request=new Request.Builder()
                .post(body)
                .url(MyEnvironment.serverBasePath+"music/loadSingerBySingerId")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message msg=new Message();
                msg.what=SingerClassificationActivity.MESSAGE_LOAD_SINGER_END;
                msg.obj=response.body().string();
                handler.sendMessage(msg);
            }
        });
    }
}
