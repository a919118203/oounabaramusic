package com.oounabaramusic.android.bean;

public class MyImage {
	public static final int TYPE_PLAY_LIST_COVER=0;
	public static final int TYPE_PLAY_LIST_TAG_ICON=1;
	public static final int TYPE_POST_IMAGE=2;
	public static final int TYPE_SINGER_COVER=3;
	public static final int TYPE_USER_BACKGROUND=4;
	public static final int TYPE_USER_HEADER=5;
	public static final int TYPE_VIDEO_COVER=6;
	private int id;
	private int contentType;
	private int contentId;
	private String md5;

	public MyImage(){}

	public MyImage(int contentType,int contentId){
		this.contentId=contentId;
		this.contentType=contentType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}	
}