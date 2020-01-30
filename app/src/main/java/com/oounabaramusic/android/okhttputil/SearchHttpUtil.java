package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.fragment.SingerMainFragment;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchHttpUtil {

    public static void searchMusic(Context context, String search, final Handler handler){

        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        String url= MyEnvironment.serverBasePath+"music/searchMusic";

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("searchText",search)
                .build();

        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();

                List<String> json= gson.fromJson(response.body().string(),new TypeToken<List<String>>(){}.getType());
                List<Music> dataList=new ArrayList<>();

                for(String m:json){
                    dataList.add(new Music(m));
                }

                Message message=new Message();
                message.what= SearchActivity.MESSAGE_SEARCH_MUSIC;
                message.obj=dataList;
                handler.sendMessage(message);
            }
        });
    }

    public static void searchMusicBySingerId(Context context, String singerId,
                                             String limit, String offset, final Handler handler){

        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        String url= MyEnvironment.serverBasePath+"music/searchMusicBySinger";

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("singerId",singerId)
                .add("limit",limit)
                .add("offset",offset)
                .build();

        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(HttpUtil.FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();

                List<String> json= gson.fromJson(response.body().string(),new TypeToken<List<String>>(){}.getType());
                List<Music> dataList=new ArrayList<>();

                for(String m:json){
                    dataList.add(new Music(m));
                }

                Message message=new Message();
                message.what= SingerMainFragment.MESSAGE_SEARCH_END;
                message.obj=dataList;
                handler.sendMessage(message);
            }
        });
    }
}
