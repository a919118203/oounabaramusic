package com.oounabaramusic.android.bean;

public class SingerType {
	private int id;
	private String type;
	public SingerType(){ }
	public SingerType(int id){
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
