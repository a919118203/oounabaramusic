package com.oounabaramusic.android.bean;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class PlayListSmallTag implements Serializable {
	private int id;
	private String name;
	private int bigId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBigId() {
		return bigId;
	}
	public void setBigId(int bigId) {
		this.bigId = bigId;
	}

	@Override
	public boolean equals(@Nullable Object obj) {

		if(obj instanceof PlayListSmallTag){
			return ((PlayListSmallTag)obj).getId()==id;
		}

		//用于小标签寻找大标签
		if(obj instanceof PlayListBigTag){
			return ((PlayListBigTag)obj).getId()==bigId;
		}
		return super.equals(obj);
	}
}
