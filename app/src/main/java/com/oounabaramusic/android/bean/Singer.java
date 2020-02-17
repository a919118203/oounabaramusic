package com.oounabaramusic.android.bean;

import java.io.Serializable;

public class Singer implements Serializable {
	private int id;
	private String singerName;
	private String type;
	private String country;
	private String introduction;
	private int fans;
	private boolean followed;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSingerName() {
		return singerName;
	}
	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public int getFans() {
		return fans;
	}
	public void setFans(int fans) {
		this.fans = fans;
	}

	public void setFollowed(boolean followed) {
		this.followed = followed;
	}
	public boolean getFollowed(){
		return followed;
	}
}
