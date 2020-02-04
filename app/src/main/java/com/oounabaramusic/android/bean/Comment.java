package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Comment implements Serializable {
	public static final int MUSIC=1;
	public static final int PLAY_LIST=2;
	public static final int VIDEO=3;
	public static final int ORDER_BY_GOOD_CNT=0;
	public static final int ORDER_BY_DATE=1;
	public static final int SEARCH_TYPE_SIMPLE=0;
	public static final int SEARCH_TYPE_DETAILED=1;

	//内容部分
	private int id;
	private String content;
	private Date date;
	private int userId;
	private String userName;
	private int goodCnt;
	private List<Reply> replies;
	private int replyCnt;
	private int gooded;

	//检索用部分
	private int mainUserId;
	private int targetId;
	private String targetMd5;
	private int targetType;
	private int orderByType;
	private int limitStart;
	private int limitLen;
	private int searchType;

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
	public List<Reply> getReplies() {
		return replies;
	}
	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public int getTargetType() {
		return targetType;
	}
	public void setTargetType(int targetType) {
		this.targetType = targetType;
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
	public int getOrderByType() {
		return orderByType;
	}
	public void setOrderByType(int orderByType) {
		this.orderByType = orderByType;
	}
	public int getLimitStart() {
		return limitStart;
	}
	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}
	public int getLimitLen() {
		return limitLen;
	}
	public void setLimitLen(int limitLen) {
		this.limitLen = limitLen;
	}
	public int getMainUserId() {
		return mainUserId;
	}
	public void setMainUserId(int mainUserId) {
		this.mainUserId = mainUserId;
	}
	public int getSearchType() {
		return searchType;
	}
	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}
	public String getTargetMd5() {
		return targetMd5;
	}

	public void setTargetMd5(String targetMd5) {
		this.targetMd5 = targetMd5;
	}

	public int getReplyCnt() {
		return replyCnt;
	}

	public void setReplyCnt(int replyCnt) {
		this.replyCnt = replyCnt;
	}
}
