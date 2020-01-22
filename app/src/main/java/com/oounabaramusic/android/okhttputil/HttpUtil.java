package com.oounabaramusic.android.okhttputil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.oounabaramusic.android.MainActivity;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static void login(String photo, final Activity activity){
        OkHttpClient client=new OkHttpClient();
        RequestBody body= new FormBody.Builder()
                .add("photo",photo)
                .build();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath +"login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.printLog("HttpUtil/login error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();
                User user=gson.fromJson(response.body().string(),User.class);

                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(activity).edit();
                editor.putString("userId", String.valueOf(user.getId()));
                editor.putString("userName",user.getUserName());
                editor.putBoolean("login",true);
                editor.apply();

                Intent intent=new Intent(activity,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }


//    public static void loadImage(final Bitmap[] bitmaps, String[] imagePaths, final Handler handler){
//        OkHttpClient client=new OkHttpClient();
//
//        final Gson gson=new Gson();
//        RequestBody body=new FormBody.Builder()
//                .add("json",gson.toJson(imagePaths))
//                .build();
//
//        Request request=new Request.Builder()
//                .url(MyEnvironment.serverBasePath+"load/header")
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
//                String json=response.headers().get("json");
//                LogUtil.printLog(json);
//                Map<String,List<String>> data= gson.fromJson(json,new TypeToken<Map<String,List<String>>>(){}.getType());
//
//                if(data==null)
//                    return;
//
//                List<String> fileLength=data.get("fileLength");
//
//                InputStream is=Objects.requireNonNull(response.body()).byteStream();
//                FileOutputStream fos=null;
//                BufferedOutputStream bos=null;
//                for(int i=0;i<fileLength.size();i++){
//
//                    File file=new File(MyEnvironment.cachePath+new Date()+".jpg");
//                    file.createNewFile();
//                    fos=new FileOutputStream(file);
//                    bos=new BufferedOutputStream(fos);
//                    int len=Integer.valueOf(fileLength.get(i));
//                    while((is.read())!=-1){
//                        bos.write(buff,0,read);
//                        readSum+=read;
//                        read=(len-readSum)>1?1:len-readSum;
//                        if(read==0)
//                            break;
//                    }
//                    bos.flush();
//                    bos.close();
//                    bitmaps[i]= BitmapFactory.decodeFile(MyEnvironment.cachePath+file.getName());
//                    handler.sendEmptyMessage(200);
//                }
//            }
//        });
//    }
}
