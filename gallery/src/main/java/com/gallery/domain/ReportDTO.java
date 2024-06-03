package com.gallery.domain;

import java.util.List;

import com.gallery.util.MyMultipartFile;

public class ReportDTO {
	private long num;
	private String subject;
	private String content;
	private String member_id;
	private String reg_date;
	private String userName;
	
	private long fileNum;
	private String uploadFilename;
	private String saveFilename;
	
	private List<MyMultipartFile> listFile; //다중 파일 처리하기 위해서

	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
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

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public long getFileNum() {
		return fileNum;
	}
	
	public void setFileNum(long fileNum) {
		this.fileNum = fileNum;
	}
	
	public String getUploadFilename() {
		return uploadFilename;
	}
	
	public void setUploadFilename(String uploadFilename) {
		this.uploadFilename = uploadFilename;
	}
	
	public String getSaveFilename() {
		return saveFilename;
	}
	
	public void setSaveFilename(String saveFilename) {
		this.saveFilename = saveFilename;
	}
	
	public List<MyMultipartFile> getListFile() {
		return listFile;
	}

	public void setListFile(List<MyMultipartFile> listFile) {
		this.listFile = listFile;
	}
}
