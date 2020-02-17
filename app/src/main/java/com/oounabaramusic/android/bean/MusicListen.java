package com.oounabaramusic.android.bean;

import java.util.Date;

public class MusicListen {
	private int musicListenId;
	
	private Date firstListenDate;
	
	//当这个值不为空时，查询大于这个值的数据
	private Date lastListenDate;
	
	private int userId;
	
	private int musicId;
	
	//当这个值为-1时，查询结果按这个列来排序
	private int listenCnt;
	
	//当这个值为-1时，删除的和未删除的都查
	private int del;                
	public int getMusicListenId() {
		return musicListenId;
	}
	public void setMusicListenId(int musicListenId) {
		this.musicListenId = musicListenId;
	}
	public Date getFirstListenDate() {
		return firstListenDate;
	}
	public void setFirstListenDate(Date firstListenDate) {
		this.firstListenDate = firstListenDate;
	}
	public Date getLastListenDate() {
		return lastListenDate;
	}
	public void setLastListenDate(Date lastListenDate) {
		this.lastListenDate = lastListenDate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getMusicId() {
		return musicId;
	}
	public void setMusicId(int musicId) {
		this.musicId = musicId;
	}
	public int getListenCnt() {
		return listenCnt;
	}
	public void setListenCnt(int listenCnt) {
		this.listenCnt = listenCnt;
	}
	public int getDel() {
		return del;
	}
	public void setDel(int del) {
		this.del = del;
	}
	
}
