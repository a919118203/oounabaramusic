package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.oounabaramusic.android.util.InternetUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 传过去的值和返回的值都是String
 */


public class S2SHttpUtil {
    public static final int MESSAGE_NO_NET=-100;
    public static final int MESSAGE_FAILURE=-2000;
    private static OkHttpClient client=new OkHttpClient();
    private Context context;
    private String url;
    private String json;
    private Handler handler;
    private boolean cancel;

    public S2SHttpUtil(Context context, String json, String url, Handler handler){
        this.context=context;
        this.json=json;
        this.url=url;
        this.handler=handler;
    }

    public void cancel() {
        cancel=true;
    }

    public void call(final int requestCode) {
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(MESSAGE_NO_NET);
            return;
        }

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(MESSAGE_FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(cancel){
                    return;
                }

                Message msg=new Message();
                msg.what=requestCode;
                msg.obj=response.body().string();
                handler.sendMessage(msg);
            }
        });
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
