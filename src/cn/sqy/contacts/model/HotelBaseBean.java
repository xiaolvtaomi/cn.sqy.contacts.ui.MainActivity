package cn.sqy.contacts.model;

import java.io.Serializable;

public class HotelBaseBean implements Serializable{
	private String iconurl;
	private String detailurl;
	private String title;
	private String area;
	private String price;
	private String info;
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	public String getDetailurl() {
		return detailurl;
	}
	public void setDetailurl(String detailurl) {
		this.detailurl = detailurl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	
}
