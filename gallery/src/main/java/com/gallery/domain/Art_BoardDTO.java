package com.gallery.domain;

public class Art_BoardDTO {

	private long num; 			// 글번호 
	private String member_id;	// 아이디
	private int hitcount; 		// 조회수
	private String reg_date;    // 작성일
	private int notice; 	    // 공지여부
	private String subject;     // 제목
	private String content;     // 내용
	
	private String saveFilename;	// 저장된파일명
	private String uploadfilename;	// 업로드 파일명
	private long fileSize;			// 파일크기
	
	private int likeCount;
	
	
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getSaveFilename() {
		return saveFilename;
	}
	public void setSaveFilename(String saveFilename) {
		this.saveFilename = saveFilename;
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
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
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
	
	
}
