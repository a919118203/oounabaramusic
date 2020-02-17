package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

public class PlayListBigTag implements Serializable {
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
