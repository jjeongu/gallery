package com.gallery.domain;

public class Fan_BoardDTO {

	private long num;
	private String member_id;
	private int notice;
	private int hitcount;
	private String reg_date;	
	private String subject;
	private String content; 
	private String name;     
	private String nickname;
	
	private String saveFilename;
	private String uploadfilename;
	private long fileSize;
	
	private int likeCount;
	
	
	
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public int getNotice() {
		return notice;
	}
	public void setNotice(int notice) {
		this.notice = notice;
	}
	public int getHitcount() {
		return hitcount;
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
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUploadfilename() {
		return uploadfilename;
	}
	public void setUploadfilename(String uploadfilename) {
		this.uploadfilename = uploadfilename;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long filesize) {
		this.fileSize = filesize;
	}
	public String getSaveFilename() {
		return saveFilename;
	}
	public void setSaveFilename(String saveFilename) {
		this.saveFilename = saveFilename;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	
	
}
