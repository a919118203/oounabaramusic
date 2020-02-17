package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StringUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VideoHttpUtil {

    public static void uploadVideo(Context context, Video video,String filePath, Handler handler){
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

        String json = new Gson().toJson(video);
        Request request= null;
        try {
            request = new Request.Builder()
                    .url(MyEnvironment.serverBasePath+"uploadVideo")
                    .header("json", URLEncoder.encode(json,"UTF-8"))  //避免中文报错
                    .header("type",filePath.substring(filePath.indexOf(".")))
                    .post(body)
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.newCall(request).enqueue(new HttpUtil.StringCallBack(handler, BasicCode.UPLOAD_VIDEO));
    }
}
