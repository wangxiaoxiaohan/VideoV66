package com.softwinner.TvdVideo.model;

public class FileVideoItem {
	private String mPath;
	private String mTitle;
	private String mTotalTime;
	
	public FileVideoItem(){
		
	}

	public FileVideoItem(String mPath, String mTitle, String mTotalTime) {
		super();
		this.mPath = mPath;
		this.mTitle = mTitle;
		this.mTotalTime = mTotalTime;
	}

	public String getmPath() {
		return mPath;
	}

	public void setmPath(String mPath) {
		this.mPath = mPath;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmTotalTime() {
		return mTotalTime;
	}

	public void setmTotalTime(String mTotalTime) {
		this.mTotalTime = mTotalTime;
	}

}
