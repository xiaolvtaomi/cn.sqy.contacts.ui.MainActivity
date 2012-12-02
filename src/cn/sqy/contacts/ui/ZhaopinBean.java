package cn.sqy.contacts.ui;

import java.io.Serializable;

public class ZhaopinBean implements Serializable{
	private String url = "";
	private String title = "";
	private String address = "";
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
