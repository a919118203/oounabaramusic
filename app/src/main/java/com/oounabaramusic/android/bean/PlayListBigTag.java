package com.oounabaramusic.android.bean;

import java.util.List;

public class PlayListBigTag {
	private int id;
	private String name;
	private List<PlayListSmallTag> tags;
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
	public List<PlayListSmallTag> getTags() {
		return tags;
	}
	public void setTags(List<PlayListSmallTag> tags) {
		this.tags = tags;
	}
	
}
