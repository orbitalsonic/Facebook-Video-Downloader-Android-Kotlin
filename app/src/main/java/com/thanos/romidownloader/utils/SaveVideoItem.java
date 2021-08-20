package com.thanos.romidownloader.utils;

import android.net.Uri;

public class SaveVideoItem
{
	private String name;
	private Uri uri;
	private String path;
	private String filename;
	
	public SaveVideoItem() {
	}
	
	public String getName() {
		return name;
	}
	
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Uri getUri() {
		return uri;
	}
	
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
