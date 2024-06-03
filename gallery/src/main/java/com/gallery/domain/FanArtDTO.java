package com.gallery.domain;

public class FanArtDTO {
	private int num;
	private String member_id;
	private int hitcount;
	private String reg_date;	
	private int notice;
	private String subject;
	private String content;
	private String img;
	private String upload_img;
	private String name;
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public int getHitcount() {
		return hitcount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setHitcount(int hitcount) {
		this.hitcount = hitcount;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	public int getNotice() {
		return notice;
	}
	public void setNotice(int notice) {
		this.notice = notice;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getUpload_img() {
		return upload_img;
	}
	public void setUpload_img(String upload_img) {
		this.upload_img = upload_img;
	}
	
}
