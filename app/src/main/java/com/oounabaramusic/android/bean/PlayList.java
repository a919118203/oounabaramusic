package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PlayList implements Serializable{
	private int id;
	private String playListName;
	private List<PlayListSmallTag> playListTag;
	private String introduction;
	private int createUserId;
	private String createUserName;
	private Date createDate;
	private int cnt;           //歌单里有多少首歌
	private int favorites;     //被收藏多少次
	private int isMyLove;      //是否是“我喜爱的音乐”歌单
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlayListName() {
		return playListName;
	}
	public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}
	public List<PlayListSmallTag> getPlayListTag() {
		return playListTag;
	}
	public void setPlayListTag(List<PlayListSmallTag> playListTag) {
		this.playListTag = playListTag;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getFavorites() {
		return favorites;
	}
	public void setFavorites(int favorites) {
		this.favorites = favorites;
	}
	public int getIsMyLove() {
		return isMyLove;
	}
	public void setIsMyLove(int isMyLove) {
		this.isMyLove = isMyLove;
	}
}
