package com.quranmp3.model;

public class Mp3PlayLists {

	private int id;
	private String name;
	private int order;
	private String date;
	private int count = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	// -----------
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// -----------
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// -------------
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}