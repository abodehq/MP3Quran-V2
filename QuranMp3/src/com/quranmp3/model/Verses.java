package com.quranmp3.model;

public class Verses {

	private int id;
	private String name;
	private int ayahCount;
	private int placeId;
	private String audioPath;
	private float size;

	// -----------
	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	// -----------
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// -------------
	public int getAyahCount() {
		return ayahCount;
	}

	public void setAyahCount(int ayahCount) {
		this.ayahCount = ayahCount;
	}

	// -------------
	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

}