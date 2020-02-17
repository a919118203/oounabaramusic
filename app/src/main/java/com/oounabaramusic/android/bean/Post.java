package com.oounabaramusic.android.bean;

import java.util.Date;

public class Post {
	public static final String THEME_TEXT="发动态：";
	public static final String THEME_FORWARD="转发：";
	public static final String THEME_MUSIC="分享音乐：";
	public static final String THEME_VIDEO="分享视频";
	public static final int MAX_LEN=200;
	public static final int TEXT=0;  //纯文本
	public static final int FORWARD=1;  //转发
	public static final int MUSIC=2;
	public static final int VIDEO=3;

	private int id;
	private int userId;
	private String userName;
	private Date date;
	private boolean hasImage;
	private int contentType;
	private int contentId;

	private int gooded;
	private int forwardCnt;
	private int commentCnt;
	private int goodCnt;

	private String content;
	private String music;
	private Post post;
	private Video video;

	//检索用
	private int start;
	private int mainUserId;  //主视角用户
	private int fromUserId;  //用于检索我关注的用户的动态

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean getHasImage() {
		return hasImage;
	}
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public int getGooded() {
		return gooded;
	}
	public void setGooded(int gooded) {
		this.gooded = gooded;
	}
	public int getForwardCnt() {
		return forwardCnt;
	}
	public void setForwardCnt(int forwardCnt) {
		this.forwardCnt = forwardCnt;
	}
	public int getCommentCnt() {
		return commentCnt;
	}
	public void setCommentCnt(int commentCnt) {
		this.commentCnt = commentCnt;
	}
	public int getGoodCnt() {
		return goodCnt;
	}
	public void setGoodCnt(int goodCnt) {
		this.goodCnt = goodCnt;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMusic() {
		return music;
	}
	public void setMusic(String music) {
		this.music = music;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getMainUserId() {
		return mainUserId;
	}
	public void setMainUserId(int mainUserId) {
		this.mainUserId = mainUserId;
	}
	public int getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(int fromUserId) {
		this.fromUserId = fromUserId;
	}
	public Video getVideo() {
		return video;
	}
	public void setVideo(Video video) {
		this.video = video;
	}
	public static String getTheme(int contentType){
		switch (contentType){
			case TEXT:
				return THEME_TEXT;
			case MUSIC:
				return THEME_MUSIC;
			case FORWARD:
				return THEME_FORWARD;
			case VIDEO:
				return  THEME_VIDEO;
		}
		return "";
	}
}

