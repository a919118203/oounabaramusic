package com.oounabaramusic.android.bean;

import java.io.Serializable;

public class Singer implements Serializable {
	private int id;
	private String singerName;
	private String type;
	private int typeId;
	private String country;
	private int countryId;
	private String introduction;
	private int fans;
	private boolean followed;

	//检索用
	private int mainUserId;
	private int start;

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
	public int getMainUserId() {
		return mainUserId;
	}
	public void setMainUserId(int mainUserId) {
		this.mainUserId = mainUserId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
