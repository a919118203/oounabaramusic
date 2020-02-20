package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.util.DigestUtils;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StringUtil;
import com.oounabaramusic.android.util.VideoUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
