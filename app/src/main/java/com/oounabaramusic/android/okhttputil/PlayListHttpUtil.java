package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayListHttpUtil {

    public static final int MESSAGE_FIND_MY_PLAY_LIST_END=0;
    public static final int MESSAGE_CREATE_END=1;
    public static final int MESSAGE_FIND_PLAY_LIST_END=2;
    public static final int MESSAGE_SAVE_NAME_END=3;
    public static final int MESSAGE_SAVE_INTRODUCTION_END=4;
    public static final int MESSAGE_UPLOAD_PL_COVER_END=5;
    public static final int MESSAGE_FIND_PLAY_LIST_MUSIC_END=6;
    public static final int MESSAGE_DELETE_MUSIC_END=7;
    public static final int MESSAGE_COLLECTION_MUSIC_EDN=8;
    public static final int MESSAGE_CANCEL_COLLECTION_EDN=9;

    public static void findPlayListByUser(Context context, String userId, Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("userId",userId)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"findPlayListByUser")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_FIND_MY_PLAY_LIST_END));
    }

    public static void findPlayList(Context context, String playListId, Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("playListId",playListId)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"findPlayList")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_FIND_PLAY_LIST_END));
    }



    public static void createPlayList(Context context, String userId , String playListName, Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("userId",userId)
                .add("playListName",playListName)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"createPlayList")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_CREATE_END));
    }

    public static void savePlayListName(Context context,String json,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"savePlayListName")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_SAVE_NAME_END));
    }

    public static void savePlayListIntroduction(Context context,String json,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"savePlayListIntroduction")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_SAVE_INTRODUCTION_END));
    }

    public static void uploadPlayListCover(Context context,String filePath,String playListId,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        File file=new File(filePath);
        if(!file.exists())
            return;

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),
                        RequestBody.create(file,MediaType.parse("multipart/form-data")))
                .build();
        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"uploadPlayListCover")
                .header("playListId",playListId)
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_UPLOAD_PL_COVER_END));
    }

    public static void addToPlayList(Context context,String json,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"addToPlayList")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_SAVE_INTRODUCTION_END));//没用
    }

    public static void findMusicByPlayList(Context context,String playListId,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("playListId",playListId)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"findMusicByPlayList")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_FIND_PLAY_LIST_MUSIC_END));
    }

    public static void deletePlayListMusic(Context context,int playListId,int musicId,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("playListId",playListId+"")
                .add("musicId",musicId+"")
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"deletePlayListMusic")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_DELETE_MUSIC_END));//没用
    }

    public static void collectionMusic(Context context,String json,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"collectionMusic")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_COLLECTION_MUSIC_EDN));
    }

    public static void cancelCollectionMusic(Context context,String json,Handler handler){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(HttpUtil.NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("json",json)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"cancelCollectionMusic")
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler,MESSAGE_CANCEL_COLLECTION_EDN));
    }
}
