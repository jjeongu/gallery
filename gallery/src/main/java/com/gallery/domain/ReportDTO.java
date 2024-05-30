package com.gallery.domain;

import java.util.List;

import com.gallery.util.MyMultipartFile;

public class ReportDTO {
	private long report_num;
	private String subject;
	private String content;
	private String member_id;
	private String reg_date;
	
	
	private long filenum;
	private String uploadfilename;
	private String savefilename;
	
	private List<MyMultipartFile> listFile; //다중 파일 처리하기 위해서

	public long getReport_num() {
		return report_num;
	}

	public void setReport_num(long report_num) {
		this.report_num = report_num;
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

	public long getFilenum() {
		return filenum;
	}

	public void setFilenum(long filenum) {
		this.filenum = filenum;
	}

	public String getUploadfilename() {
		return uploadfilename;
	}

	public void setUploadfilename(String uploadfilename) {
		this.uploadfilename = uploadfilename;
	}

	public String getSavefilename() {
		return savefilename;
	}

	public void setSavefilename(String savefilename) {
		this.savefilename = savefilename;
	}

	public List<MyMultipartFile> getListFile() {
		return listFile;
	}

	public void setListFile(List<MyMultipartFile> listFile) {
		this.listFile = listFile;
	}
	
	
	
}
