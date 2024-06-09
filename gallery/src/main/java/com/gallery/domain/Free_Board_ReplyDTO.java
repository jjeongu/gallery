package com.gallery.domain;

public class Free_Board_ReplyDTO {

	private long r_num;
	private long num;
	private String content;
	private String reg_date;
	private String member_id;
	private long answer;
	
	private String name;
	private int replyLike;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getReplyLike() {
		return replyLike;
	}
	public void setReplyLike(int replyLike) {
		this.replyLike = replyLike;
	}
	public int getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	private int answerCount;
	private int likeCount;
	
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public long getAnswer() {
		return answer;
	}
	public void setAnswer(long answer) {
		this.answer = answer;
	}
	public long getR_num() {
		return r_num;
	}
	public void setR_num(long r_num) {
		this.r_num = r_num;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	
	
}
