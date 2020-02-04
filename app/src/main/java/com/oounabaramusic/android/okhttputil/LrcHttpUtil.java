package com.oounabaramusic.android.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LrcHttpUtil {
    private Context context;
    private String musicMd5;
    private Handler handler;

    public LrcHttpUtil(Context context, String musicMd5, Handler handler){
        this.context=context;
        this.musicMd5=musicMd5;
        this.handler=handler;
    }

    public void call(int requestCode){
        if(!InternetUtil.checkNet(context)){
            Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(S2SHttpUtil.MESSAGE_NO_NET);
            return;
        }

        OkHttpClient client=new OkHttpClient();

        RequestBody body=new FormBody.Builder()
                .add("md5",musicMd5)
                .build();

        Request request=new Request.Builder()
                .post(body)
                .url(MyEnvironment.serverBasePath+"downloadLrc")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(S2SHttpUtil.MESSAGE_FAILURE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String exists=response.header("exists");

                assert exists != null;
                if(exists.equals("0")){
                    handler.sendEmptyMessage(BasicCode.NO_LRC);
                    return ;
                }

                File file=new File(MyEnvironment.musicLrc+musicMd5+".lrc");
                file.createNewFile();

                InputStream is = response.body().byteStream();
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file));

                byte[] lins=new byte[1024*1024];
                int len;
                while((len=is.read(lins))!=-1){
                    bos.write(lins,0,len);
                }
                bos.flush();
                bos.close();
                is.close();

                handler.sendEmptyMessage(BasicCode.DOWNLOAD_END);
            }
        });
    }
}
