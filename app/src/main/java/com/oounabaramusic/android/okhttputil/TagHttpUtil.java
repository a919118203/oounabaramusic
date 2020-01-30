package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TagHttpUtil {

    public static final int MESSAGE_GET_PLAY_LIST_TAG_END=0;
    public static final int MESSAGE_SAVE_PLAY_LIST_TAG_END=1;

    private static OkHttpClient client=new OkHttpClient();

    public static void getPlayListTag(Context context, Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"loadPlayListTag")
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_GET_PLAY_LIST_TAG_END));
    }

    public static void savePlayListTag(Context context,String json, Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .post(body)
                .url(MyEnvironment.serverBasePath+"savePlayListTag")
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_SAVE_PLAY_LIST_TAG_END));
    }
}
