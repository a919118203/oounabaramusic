package com.oounabaramusic.android.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.ForwardActivity;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.VideoUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.customview.MyVideoPlayer;
import com.oounabaramusic.android.widget.popupwindow.ShowImageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final String CHANGE_GOOD="a";
    private BaseActivity activity;
    private List<Post> dataList;

    //动作的位置  如：删除,点赞等
    private int selectPosition;

    private Bitmap good,noGood;

    public PostAdapter(BaseActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();

        good = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.good);
        noGood = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.no_good);
    }

    public void setDataList(List<Post> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Post> dataList){
        if(dataList==null||dataList.isEmpty()){
            return;
        }

        for(Post item : dataList){
            LogUtil.printLog("postId:  "+item.getId());
        }

        int start = this.dataList.size();
        int len = dataList.size();
        this.dataList.addAll(dataList) ;
        notifyItemRangeInserted(start,len);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{

            String option = (String) payloads.get(0);
            switch (option){
                case CHANGE_GOOD:
                    if(dataList.get(position).getGooded()>0){
                        holder.good.setImageBitmap(good);
                    }else{
                        holder.good.setImageBitmap(noGood);
                    }

                    if(dataList.get(position).getGoodCnt()>0){
                        holder.goodCnt.setText(FormatUtil.numberToString(
                                dataList.get(position).getGoodCnt()));
                    }else{
                        holder.goodCnt.setText("赞");
                    }
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = dataList.get(position);

        holder.userHeader.setImage(new MyImage(MyImage.TYPE_USER_HEADER,post.getUserId()));
        holder.userName.setText(post.getUserName());
        holder.theme.setText(Post.getTheme(post.getContentType()));
        holder.date.setText(FormatUtil.DateTimeToString(post.getDate()));
        holder.content.setText(post.getContent());

        //如果点赞了就显示点赞
        if(post.getGooded()>0){
            holder.good.setImageBitmap(good);
        }else{
            holder.good.setImageBitmap(noGood);
        }

        if(post.getForwardCnt()>0){
            holder.forwardCnt.setText(FormatUtil.numberToString(post.getForwardCnt()));
        }else{
            holder.forwardCnt.setText("转发");
        }

        if(post.getCommentCnt()>0){
            holder.commentCnt.setText(FormatUtil.numberToString(post.getCommentCnt()));
        }else{
            holder.commentCnt.setText("评论");
        }

        if(post.getGoodCnt()>0){
            holder.goodCnt.setText(FormatUtil.numberToString(post.getGoodCnt()));
        }else{
            holder.goodCnt.setText("赞");
        }

        //有图片的话设置图片
        if(post.getHasImage()){
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImage(new MyImage(MyImage.TYPE_POST_IMAGE,post.getId()));
        }else{
            holder.image.setVisibility(View.GONE);
        }

        //初始化
        holder.music.setVisibility(View.GONE);
        holder.forward.setVisibility(View.GONE);
        holder.video.setVisibility(View.GONE);
        holder.innerImage.setVisibility(View.GONE);
        holder.innerMusic.setVisibility(View.GONE);
        holder.innerVideo.setVisibility(View.GONE);

        switch (post.getContentType()){
            case Post.MUSIC:
                Music item = new Music(post.getMusic());
                holder.music.setVisibility(View.VISIBLE);
                holder.musicCover.setImage(new MyImage(
                        MyImage.TYPE_SINGER_COVER,
                        Integer.valueOf(item.getSingerId().split("/")[0])));
                holder.musicName.setText(item.getMusicName());
                holder.singerName.setText(item.getSingerName().replace("/"," "));
                break;

            case Post.VIDEO:
                holder.video.setVisibility(View.VISIBLE);
                Video video = post.getVideo();
                holder.video.setVideo(video);
                break;

            case Post.FORWARD:
                holder.forward.setVisibility(View.VISIBLE);

                Post index=post.getPost();
                holder.innerContent.setText("");
                while(index!=null){
                    SpannableString user = new SpannableString(" @"+index.getUserName());
                    final Post finalIndex = index;
                    user.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            startUserInfoActivity(finalIndex.getUserId());
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setUnderlineText(false);
                            ds.setColor(activity.getResources().getColor(R.color.colorPrimary));
                        }
                    },0,user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.innerContent.append(user);
                    holder.innerContent.append("：");
                    holder.innerContent.append(index.getContent());
                    if(index.getContentType()!=Post.FORWARD){
                        break;
                    }
                    index=index.getPost();
                }

                if(index!=null){
                    if(index.getHasImage()){
                        holder.innerImage.setVisibility(View.VISIBLE);
                        holder.innerImage.setImage(
                                new MyImage(MyImage.TYPE_POST_IMAGE,index.getId()));
                    }
                    if(index.getMusic()!=null){
                        Music m = new Music(index.getMusic());
                        holder.innerMusic.setVisibility(View.VISIBLE);
                        holder.innerMusicCover.setImage(new MyImage(
                                MyImage.TYPE_SINGER_COVER,
                                Integer.valueOf(m.getSingerId().split("/")[0])));
                        holder.innerMusicName.setText(m.getMusicName());
                        holder.innerSingerName.setText(m.getSingerName().replace("/"," "));
                    }
                    if(index.getVideo()!=null){
                        Video v = index.getVideo();
                        holder.innerVideo.setVisibility(View.VISIBLE);
                        holder.innerVideo.setVideo(v);
                    }
                }else{
                    holder.innerContent.append("\n从这以后的动态已被转移到了虚空...");
                }
                break;
        }
    }

    public void startUserInfoActivity(int userId){
        //如果是本活动的userId，那就只是回到主页而不是新开活动
        if(activity instanceof  UserInfoActivity){
            if(userId==((UserInfoActivity)activity).getContentUserId()){
                ((UserInfoActivity)activity).moveToMain();
                return;
            }
        }

        UserInfoActivity.startActivity(activity,userId);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView userHeader;
        TextView userName;
        TextView theme;
        TextView date;
        TextView content;

        MyImageView image;

        RelativeLayout music;
        MyImageView musicCover;
        TextView musicName;
        TextView singerName;

        MyVideoPlayer video;

        LinearLayout forward;
        TextView innerContent;
        MyImageView innerImage;
        RelativeLayout innerMusic;
        MyImageView innerMusicCover;
        TextView innerMusicName;
        TextView innerSingerName;
        MyVideoPlayer innerVideo;

        LinearLayout toForward;
        TextView forwardCnt;
        LinearLayout toComment;
        TextView commentCnt;
        LinearLayout toGood;
        ImageView good;
        TextView goodCnt;
        LinearLayout toDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,dataList.get(getAdapterPosition()).getId());
                }
            });

            userHeader=itemView.findViewById(R.id.user_header);
            userName=itemView.findViewById(R.id.user_name);
            theme=itemView.findViewById(R.id.theme);
            date=itemView.findViewById(R.id.post_time);
            content=itemView.findViewById(R.id.post_content);
            image=itemView.findViewById(R.id.post_image);
            music=itemView.findViewById(R.id.post_music);
            musicCover=itemView.findViewById(R.id.music_cover);
            musicName=itemView.findViewById(R.id.music_name);
            singerName=itemView.findViewById(R.id.music_singer);
            forward=itemView.findViewById(R.id.inner);
            innerContent=itemView.findViewById(R.id.inner_content);
            innerImage=itemView.findViewById(R.id.inner_image);
            innerMusic=itemView.findViewById(R.id.inner_music);
            innerMusicCover=itemView.findViewById(R.id.forward_music_cover);
            innerMusicName=itemView.findViewById(R.id.forward_music_name);
            innerSingerName=itemView.findViewById(R.id.forward_music_singer);
            toForward=itemView.findViewById(R.id.post_forward);
            forwardCnt=itemView.findViewById(R.id.forward_cnt);
            toComment=itemView.findViewById(R.id.post_comment);
            commentCnt=itemView.findViewById(R.id.comment_cnt);
            toGood=itemView.findViewById(R.id.post_good);
            goodCnt=itemView.findViewById(R.id.good_cnt);
            good=itemView.findViewById(R.id.good);
            toDelete=itemView.findViewById(R.id.post_delete);
            video=itemView.findViewById(R.id.video);
            innerVideo=itemView.findViewById(R.id.inner_video);

            //初始化video大小
            int width = DensityUtil.getDisplayWidth(activity)-DensityUtil.dip2px(activity,90);
            video.getLayoutParams().height= (int)(9f/16f*(double)width);
            video.getLayoutParams().width=width;
            video.requestLayout();

            //初始化里面video大小
            width = DensityUtil.getDisplayWidth(activity)-DensityUtil.dip2px(activity,110);
            innerVideo.getLayoutParams().height= (int)(9f/16f*(double)width);
            innerVideo.getLayoutParams().width=width;
            innerVideo.requestLayout();

            //使内容里的用户名可点
            innerContent.setMovementMethod(LinkMovementMethod.getInstance());

            //可删除
            if(activity instanceof  UserInfoActivity){
                if(((UserInfoActivity)activity).getContentUserId()== SharedPreferencesUtil.getUserId(activity.sp)){
                    toDelete.setVisibility(View.VISIBLE);
                    toDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPosition=getAdapterPosition();
                            Post item = dataList.get(selectPosition);
                            Post p = new Post();
                            p.setId(item.getId());
                            p.setContentType(item.getContentType());
                            p.setContentId(item.getContentId());

                            new S2SHttpUtil(
                                    activity,
                                    new Gson().toJson(p),
                                    MyEnvironment.serverBasePath+"deletePost",
                                    new MyHandler(PostAdapter.this))
                                    .call(BasicCode.DELETE_POST);
                        }
                    });
                }
            }


            //转发
            toForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ForwardActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });

            toComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,dataList.get(getAdapterPosition()).getId());
                }
            });

            toGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();

                    Map<String,Integer> data = new HashMap<>();
                    data.put("userId",SharedPreferencesUtil.getUserId(activity.sp));
                    data.put("postId",dataList.get(selectPosition).getId());

                    new S2SHttpUtil(
                            activity,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"postToGood",
                            new MyHandler(PostAdapter.this))
                    .call(BasicCode.TO_GOOD_END);
                }
            });

            //主头像
            userHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserInfoActivity(dataList.get(getAdapterPosition()).getUserId());
                }
            });

            //主用户名
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserInfoActivity(dataList.get(getAdapterPosition()).getUserId());
                }
            });

            //转发的内容
            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取最里层的动态ID
                    Post post = dataList.get(getAdapterPosition()).getPost();
                    while (post!=null){
                        if(post.getContentType()==Post.FORWARD){
                            post=post.getPost();
                        }else{
                            break;
                        }
                    }

                    if(post==null){
                        Toast.makeText(activity, "该动态已被删除", Toast.LENGTH_SHORT).show();
                    }else{
                        PostActivity.startActivity(activity,post.getId());
                    }
                }
            });
            innerContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取最里层的动态ID
                    Post post = dataList.get(getAdapterPosition()).getPost();
                    while (post!=null){
                        if(post.getContentType()==Post.FORWARD){
                            post=post.getPost();
                        }else{
                            break;
                        }
                    }

                    if(post==null){
                        Toast.makeText(activity, "该动态已被删除", Toast.LENGTH_SHORT).show();
                    }else{
                        PostActivity.startActivity(activity,post.getId());
                    }
                }
            });

            //我分享的音乐
            music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String musicStr = dataList.get(getAdapterPosition()).getMusic();
                    activity.getBinder().playMusic(new Music(musicStr));
                }
            });

            //转发分享的音乐
            innerMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取最内层的动态里的音乐
                    Post post = dataList.get(getAdapterPosition()).getPost();
                    while (post.getPost()!=null){
                        post=post.getPost();
                    }
                    String musicStr = post.getMusic();
                    activity.getBinder().playMusic(new Music(musicStr));
                }
            });

            //我的图片
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = dataList.get(getAdapterPosition());
                    new ShowImageDialog(activity,new MyImage(MyImage.TYPE_POST_IMAGE,post.getId()))
                            .show();
                }
            });

            //转发的图片
            innerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取最里层的动态ID
                    Post post = dataList.get(getAdapterPosition()).getPost();
                    while (post.getPost()!=null){
                        post=post.getPost();
                    }

                    new ShowImageDialog(activity,new MyImage(MyImage.TYPE_POST_IMAGE,post.getId()))
                    .show();
                }
            });
        }
    }

    static class MyHandler extends Handler{
        PostAdapter adapter;
        MyHandler(PostAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.DELETE_POST:
                    adapter.dataList.remove(adapter.selectPosition);
                    adapter.notifyItemRemoved(adapter.selectPosition);
                    Toast.makeText(adapter.activity, "删除成功", Toast.LENGTH_SHORT).show();
                    break;

                case BasicCode.TO_GOOD_END:
                    Map<String,Integer> result = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());
                    int gooded = result.get("gooded");
                    int goodCnt = result.get("goodCnt");
                    adapter.dataList.get(adapter.selectPosition).setGooded(gooded);
                    adapter.dataList.get(adapter.selectPosition).setGoodCnt(goodCnt);
                    adapter.notifyItemChanged(adapter.selectPosition,CHANGE_GOOD);
                    break;
            }
        }
    }
}
