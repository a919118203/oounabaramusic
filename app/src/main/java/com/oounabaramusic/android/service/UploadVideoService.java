package com.oounabaramusic.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.okhttputil.UploadRequestBody;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.core.app.NotificationCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadVideoService extends Service {

    private int notiId = 19971006;
    private Notification notification;
    private NotificationManager notificationManager;
    private Handler mHandler;

    private String json;
    private String filePath;

    private int cnt;

    public static boolean running;

    public UploadVideoService() {
        mHandler=new MyHandler(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        cnt=0;

        initNotificationManager();

        initNotification();
    }

    private void initNotificationManager(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O&&
                notificationManager.getNotificationChannel(MusicPlayService.PUSH_CHANNEL_ID)==null) {
            NotificationChannel channel = new NotificationChannel(
                    MusicPlayService.PUSH_CHANNEL_ID,
                    MusicPlayService.PUSH_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_NONE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        json = intent.getStringExtra("json");
        filePath = intent.getStringExtra("filePath");
        running=true;

        startUpload();

        return super.onStartCommand(intent, flags, startId);
    }

    private RemoteViews initContent(){
        return new RemoteViews(getPackageName(), R.layout.special_upload_notification);
    }

    private void initNotification(){
        notification = new NotificationCompat.Builder(this,MusicPlayService.PUSH_CHANNEL_ID)
                .setCustomContentView(initContent())
                .setSmallIcon(R.mipmap.default_image)
                .setTicker("Mogeko")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle("正在上传...")
                .setWhen(System.currentTimeMillis())
                .build();
    }

    private void startUpload(){
        startForeground(notiId,notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                upload();
            }
        }).start();

    }

    private void setProgress(long current,long fileSize){

        if(cnt++==50){
            cnt=0;
            initNotification();
        }

        //更新进度
        notification
                .contentView
                .setProgressBar(R.id.pb,(int)fileSize,(int)current,false);

        notification
                .contentView
                .setTextViewText(R.id.progress, FormatUtil.progressFormat(current,fileSize));

        notificationManager.notify(notiId,notification);
    }

    private void uploadVideoCover (int videoId){
        HttpUtil.uploadVideoCover(
                this,
                new MyImage(MyImage.TYPE_VIDEO_COVER,videoId),
                filePath,
                mHandler);
    }

    private void upload(){
        if(!InternetUtil.checkNet(this)){
            Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
            return;
        }

        final File file=new File(filePath);
        if(!file.exists())
            return;

        UploadRequestBody requestBody = new UploadRequestBody(RequestBody.create(file, MediaType.parse("multipart/form-data")),
                new UploadRequestBody.WriteListener(){
                    @Override
                    public void onWrite(long byteCount, long fileSize) {
                        setProgress(byteCount,fileSize);
                    }
                });

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),
                        requestBody)
                .build();

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

        if(request==null){
            return;
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message msg = new Message();
                msg.what = BasicCode.UPLOAD_VIDEO;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
            }
        });
    }

    static class MyHandler extends Handler{
        UploadVideoService service;
        MyHandler(UploadVideoService service){
            this.service=service;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.UPLOAD_VIDEO:
                    int videoId = Integer.valueOf((String) msg.obj);
                    service.uploadVideoCover(videoId);
                    break;

                case BasicCode.UPLOAD_IMAGE:
                    Toast.makeText(service, "上传成功", Toast.LENGTH_SHORT).show();

                    running=false;
                    service.stopSelf();
                    break;
            }
        }
    }
}
