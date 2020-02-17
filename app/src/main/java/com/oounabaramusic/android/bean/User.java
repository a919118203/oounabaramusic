package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class User implements Serializable {
	private int id;

	//电话号码
	private String photo;

	//用户名
	private String userName;

	//性别
	private String sex;

	//生日
	private Date birthday;

	//简介
	private String introduction;

	//账号创建时间
	private Date createTime;

	//关注数
	private int toFollowCnt;

	//粉丝数
	private int followedCnt;

	//累计听歌多少首
	private int listenMusicCnt;

	//收藏的歌的数量
	private int collectionCnt;

	//创建个歌单的个数
	private int createPlayListCnt;

	//收藏的歌单的个数
	private int collectionPlayListCnt;

	//我创建的歌单
	private List<PlayList> myCreatePlayList;

	//我收藏的歌单
	private List<PlayList> myCollectionPlayList;

	//是否关注了这个用户
	private boolean followed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getToFollowCnt() {
		return toFollowCnt;
	}

	public void setToFollowCnt(int toFollowCnt) {
		this.toFollowCnt = toFollowCnt;
	}

	public int getFollowedCnt() {
		return followedCnt;
	}

	public void setFollowedCnt(int followedCnt) {
		this.followedCnt = followedCnt;
	}

	public int getCollectionCnt() {
		return collectionCnt;
	}

	public void setCollectionCnt(int collectionCnt) {
		this.collectionCnt = collectionCnt;
	}

	public int getListenMusicCnt() {
		return listenMusicCnt;
	}

	public void setListenMusicCnt(int listenMusicCnt) {
		this.listenMusicCnt = listenMusicCnt;
	}

	public int getCreatePlayListCnt() {
		return createPlayListCnt;
	}

	public void setCreatePlayListCnt(int createPlayListCnt) {
		this.createPlayListCnt = createPlayListCnt;
	}

	public int getCollectionPlayListCnt() {
		return collectionPlayListCnt;
	}

	public void setCollectionPlayListCnt(int collectionPlayListCnt) {
		this.collectionPlayListCnt = collectionPlayListCnt;
	}

	public List<PlayList> getMyCreatePlayList() {
		return myCreatePlayList;
	}

	public void setMyCreatePlayList(List<PlayList> myCreatePlayList) {
		this.myCreatePlayList = myCreatePlayList;
	}

	public List<PlayList> getMyCollectionPlayList() {
		return myCollectionPlayList;
	}

	public void setMyCollectionPlayList(List<PlayList> myCollectionPlayList) {
		this.myCollectionPlayList = myCollectionPlayList;
	}



	public String toJson() {
		Gson gson=new Gson();
		return gson.toJson(this);
	}

	public boolean getFollowed() {
		return followed;
	}

	public void setFollowed(boolean followed) {
		this.followed = followed;
	}
}
