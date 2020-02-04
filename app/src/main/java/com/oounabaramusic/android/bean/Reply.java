package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.Date;

public class Reply implements Serializable {
	private int id;
	private String content;
	private Date date;
	private int userId;
	private String userName;
	private int goodCnt;
	private int replyTo;
	private String replyToUserName;
	private int commentId;
	private int gooded;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGoodCnt() {
		return goodCnt;
	}
	public void setGoodCnt(int goodCnt) {
		this.goodCnt = goodCnt;
	}
	public int getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getGooded() {
		return gooded;
	}

	public void setGooded(int gooded) {
		this.gooded = gooded;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReplyToUserName() {
		return replyToUserName;
	}

	public void setReplyToUserName(String replyToUserName) {
		this.replyToUserName = replyToUserName;
	}
}
