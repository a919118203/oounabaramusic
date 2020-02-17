package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserHttpUtil {

    public static void uploadUserBackground(Context context, String filePath, String userId, Handler handler){
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
                        RequestBody.create(file, MediaType.parse("multipart/form-data")))
                .build();
        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"uploadUserBackground")
                .header("userId",userId)
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler, BasicCode.UPLOAD_USER_BACKGROUND_END));
    }

    public static void uploadUserHeader(Context context, String filePath, String userId, Handler handler){
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
                        RequestBody.create(file, MediaType.parse("multipart/form-data")))
                .build();
        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"uploadUserHeader")
                .header("userId",userId)
                .post(body)
                .build();

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler, BasicCode.UPLOAD_USER_HEADER_END));
    }
}
