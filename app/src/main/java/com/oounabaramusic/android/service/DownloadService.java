package com.oounabaramusic.android.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadService extends Service {

    public static final int STATUS_STARTING=0;
    public static final int STATUS_NO_TASK=1;
    public static final int EVENT_DOWNLOAD_END=2;
    public static final int EVENT_THREAD_END=3;
    public static final int MESSAGE_PROGRESS_UP=3;
    public static final int MESSAGE_START_TASK=4;
    public static final int MESSAGE_STOP_DOWNLOAD=5;
    public static final int MESSAGE_ADD_TO_TASK=6;
    public static final int MESSAGE_DOWNLOAD_END=7;
    private int status;
    private LinkedList<Music> downloadList;
    private LinkedList<Boolean> downloadable;
    private List<Handler> handlers;
    private List<RecyclerView.Adapter> adapters;
    private int currentPosition;

    private MediaPlayer mp;
    private DownloadThread downloadThread;
    private LocalMusicDao localMusicDao;
    private DownloadHandler handler;

    private int deletePosition;   //用来辅助删除任务   初始： -1       删除全部   -2
    @Override
    public void onCreate() {
        super.onCreate();

        init();
        LogUtil.printLog("onCreate:  DownloadService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(downloadThread!=null&&downloadThread.isAlive()){
            downloadThread.stopThread(-1);
        }
        LogUtil.printLog("onDestroy:  DownloadService");
    }

    private void init() {
        downloadThread=new DownloadThread();
        localMusicDao=new LocalMusicDao(this);
        handler=new DownloadHandler(this);

        downloadList=new LinkedList<>(localMusicDao.selectAllNeedDownload());
        downloadable=new LinkedList<>();
        adapters=new ArrayList<>();
        handlers=new ArrayList<>();
        mp=new MediaPlayer();
        for(Music item:downloadList){
            downloadable.add(false);
        }
        currentPosition=-1;
        deletePosition=-1;
        status=STATUS_NO_TASK;
    }

    private DownloadBinder binder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(binder==null){
            binder=new DownloadBinder();
        }
        return binder;
    }

    private boolean isAlive;//Thread.isAlive() 不靠谱
    private void startNewTask(int position){
        if(isAlive){
            LogUtil.printLog("前一个线程还没结束，下一个应该运行："+position);
            downloadThread.stopThread(position);   //等前一个线程完全结束再开始这次的下载
            return ;
        }


        LogUtil.printLog("开始：  "+position);
        downloadThread=new DownloadThread();
        currentPosition=position;
        status=STATUS_STARTING;
        downloadThread.start();
    }

    private int getNext(){
        int startIndex=currentPosition;

        int i=(startIndex+1)%downloadable.size();
        while(i!=startIndex&&!downloadable.get(i)){
            i=(i+1)%downloadable.size();
        }
        if(i!=startIndex){
            return i;
        }else{
            return -1;
        }
    }

    private class DownloadThread extends Thread{

        boolean running;
        int nextDownload;

        DownloadThread(){
            running=true;
        }


        public void stopThread(int nextDownload){
            running=false;
            this.nextDownload=nextDownload;
        }

        @Override
        public void run() {

            isAlive=true;

            Music item=downloadList.get(currentPosition);

            File file = new File(item.getFilePath());

            OkHttpClient client=new OkHttpClient();

            RequestBody body=new FormBody.Builder()
                    .add("md5",item.getMd5())
                    .add("fileLoaded",String.valueOf(file.length()))  //续下载
                    .build();

            Request request=new Request.Builder()
                    .url(MyEnvironment.serverBasePath+"/music/downloadMusic")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                InputStream is=response.body().byteStream();
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file,true));

                for(Handler handler:handlers){
                    Message msg=new Message();
                    msg.what=MESSAGE_START_TASK;
                    msg.arg1=currentPosition;
                    handler.sendMessage(msg);
                }

                byte[] bytes=new byte[1024*1024];
                int len;
                long total=file.length();

                while((len=is.read(bytes))!=-1){
                    bos.write(bytes,0,len);
                    total+=(long)len;

                    Thread.sleep(50);//TODO下载速度太快  不好看效果

                    //每一次进度都发送消息
                    for(Handler handler:handlers){
                        Message msg=new Message();
                        msg.what=MESSAGE_PROGRESS_UP;
                        msg.obj=total;
                        handler.sendMessage(msg);
                    }

                    //强制停止
                    if(!running){

                        downloadable.set(currentPosition,false);

                        for(Handler handler:handlers){
                            Message msg=new Message();
                            msg.what=MESSAGE_STOP_DOWNLOAD;
                            msg.arg1=currentPosition;
                            handler.sendMessage(msg);
                        }

                        bos.flush();
                        bos.close();
                        is.close();
                        isAlive=false;

                        Message msg=new Message();
                        msg.arg1=nextDownload;
                        msg.what=EVENT_THREAD_END;
                        handler.sendMessage(msg);
                        return;
                    }

                }

                bos.flush();
                bos.close();
                is.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            //下载完成后，获取音乐时长
            mp.reset();
            try {
                mp.setDataSource(file.getPath());
                mp.prepare();
                localMusicDao.updateDuration(item.getMd5(),mp.getDuration()/1000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            isAlive=false;

            //下载完成
            Message msg=new Message();
            msg.what=EVENT_DOWNLOAD_END;
            handler.sendMessage(msg);
        }
    }

    public class DownloadBinder extends Binder{

        synchronized public void addTask(Music item){

            if(downloadList.contains(item)){
                int index = downloadList.indexOf(item);
                downloadable.set(index,true);

                for(Handler handler:handlers){
                    handler.sendEmptyMessage(MESSAGE_ADD_TO_TASK);
                }

                if(status==STATUS_NO_TASK){
                    startNewTask(index);
                }
                return;
            }

            Music clone=localMusicDao.selectMusicByMd5(item.getMd5());
            if(clone!=null){
                int status = clone.getDownloadStatus();
                if(status==0||status==3){
                    Toast.makeText(DownloadService.this, "该文件已存在", Toast.LENGTH_SHORT).show();
                    return ;
                }
            }else{
                clone=item.cloneItem();
                String musicName=clone.getFilePath().substring(clone.getFilePath().lastIndexOf("/")+1);
                clone.setFilePath(MyEnvironment.musicPath+musicName);
                clone.setDownloadStatus(2);
            }

            localMusicDao.insertLocalMusic(clone);
            downloadList.add(clone);
            downloadable.add(true);


            for(Handler handler:handlers){
                handler.sendEmptyMessage(MESSAGE_ADD_TO_TASK);
            }

            if(status==STATUS_NO_TASK){
                startNewTask(0);
                Toast.makeText(DownloadService.this, "开始下载", Toast.LENGTH_SHORT).show();
            }
        }

        synchronized public void stopTask(int position){

            if(currentPosition==position){
                int index=getNext();
                if(index!=-1){
                    startNewTask(index);
                }else{
                    downloadThread.stopThread(-1);
                }
            }else{
                downloadable.set(position,false);
                for(Handler handler:handlers){
                    Message msg=new Message();
                    msg.what=MESSAGE_STOP_DOWNLOAD;
                    msg.arg1=position;
                    handler.sendMessage(msg);
                }
            }
            LogUtil.printLog("stopTask");
        }

        synchronized public void deleteTask(int position){


            //删除记录
            localMusicDao.deleteMusicByMd5(downloadList.get(position).getMd5());

            //获取文件路径
            String filePath=downloadList.get(position).getFilePath();
            File file=new File(filePath);

            if(status==STATUS_NO_TASK){
                downloadList.remove(position);
                downloadable.remove(position);

                if(file.exists()){
                    file.delete();
                }
            }else{
                if(currentPosition==position){

                    //先把线程停下来
                    stopTask(position);

                    //再想办法删除
                    deletePosition=position;

                }else{
                    downloadList.remove(position);
                    downloadable.remove(position);

                    if(position<currentPosition)
                        currentPosition--;

                    if(file.exists()){
                        file.delete();
                    }
                }
            }

            for(RecyclerView.Adapter adapter:adapters){
                adapter.notifyDataSetChanged();
            }


        }

        synchronized public void deleteAllTask(){
            deletePosition=-2;

            if(isAlive){

                downloadThread.stopThread(-1);
            }else{

                for(Music item:downloadList){
                    localMusicDao.deleteMusicByMd5(item.getMd5());

                    File file=new File(item.getFilePath());
                    if(file.exists()){
                        file.delete();
                    }
                }

                downloadList.clear();
                downloadable.clear();
                for(RecyclerView.Adapter adapter:adapters){
                    adapter.notifyDataSetChanged();
                }
            }
        }

        public LinkedList<Music> getDownloadList(){
            return downloadList;
        }

        public LinkedList<Boolean> getDownloadable(){
            return downloadable;
        }

        public int getStatus(){
            return status;
        }

        public void addHandler(Handler handler){
            handlers.add(handler);
        }

        public void removeHandler(Handler handler){
            handlers.remove(handler);
        }

        public int getPosition(){
            return currentPosition;
        }

        public void addAdapter(RecyclerView.Adapter adapter){
            adapters.add(adapter);
        }
    }

    static class DownloadHandler extends Handler{

        DownloadService downloadService;

        public DownloadHandler(DownloadService downloadService){
            this.downloadService=downloadService;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_DOWNLOAD_END:
                    Music end=downloadService.downloadList.remove(downloadService.currentPosition);
                    downloadService.downloadable.remove(downloadService.currentPosition);

                    //修改下载状态为'已下载'
                    downloadService.localMusicDao.updateDownloadStatus(end.getMd5(),0);

                    Toast.makeText(downloadService, end.getMusicName()+"已经下载完成", Toast.LENGTH_SHORT).show();

                    int startIndex=-1;
                    int currentPosition=downloadService.currentPosition;
                    downloadService.currentPosition=-1;

                    //必须要在这同步更新adapter
                    for(RecyclerView.Adapter adapter:downloadService.adapters){
                        adapter.notifyDataSetChanged();
                    }

                    if(downloadService.downloadList.size()>currentPosition){
                        startIndex=currentPosition;
                    }else{
                        if(downloadService.downloadList.size()>0){
                            startIndex=0;
                        }else{
                            downloadService.currentPosition=-1;
                            downloadService.status=STATUS_NO_TASK;
                        }
                    }

                    if(startIndex!=-1){
                        if(downloadService.downloadable.get(startIndex)){
                            downloadService.startNewTask(startIndex);
                        }else{
                            int i=(startIndex+1)%downloadService.downloadable.size();
                            while(i!=startIndex&&!downloadService.downloadable.get(i)){
                                i=(i+1)%downloadService.downloadable.size();
                            }
                            if(i!=startIndex){
                                downloadService.startNewTask(i);
                            }else{
                                downloadService.currentPosition=-1;
                                downloadService.status=STATUS_NO_TASK;
                            }
                        }
                    }
                    break;
                case EVENT_THREAD_END:

                    int index=downloadService.deletePosition;
                    downloadService.deletePosition=-1;

                    //线程结束时判断需不需要删
                    if(index!=-1){
                        if(index!=-2){

                            if(msg.arg1>index){
                                msg.arg1--;
                            }

                            Music item=downloadService.downloadList.remove(index);
                            downloadService.downloadable.remove(index);

                            for(RecyclerView.Adapter adapter:downloadService.adapters){
                                adapter.notifyDataSetChanged();
                            }

                            File file=new File(item.getFilePath());
                            if(file.exists()){
                                file.delete();
                            }
                        }else{
                            for(Music item:downloadService.downloadList){
                                downloadService.localMusicDao.deleteMusicByMd5(item.getMd5());

                                File file=new File(item.getFilePath());
                                if(file.exists()){
                                    file.delete();
                                }
                            }

                            downloadService.downloadList.clear();
                            downloadService.downloadable.clear();
                            for(RecyclerView.Adapter adapter:downloadService.adapters){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }


                    if(msg.arg1!=-1&&msg.arg1<downloadService.downloadList.size()){
                        downloadService.startNewTask(msg.arg1);
                    }else{
                        downloadService.currentPosition=-1;
                        downloadService.status=STATUS_NO_TASK;
                    }
                    break;
            }
        }
    }
}
