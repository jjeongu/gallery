package com.gallery.domain;

public class Art_Board_ReplyDTO {
	private long r_num; // 댓글 번호
	private long num; // 글 번호
	private String member_id; // 아이디(작성자)
	private String name;	// 이름
	private String content; // 내용
	private String reg_date;
	private long answer;
	private int replyLike;
	
	private int answerCount;
	private int likeCount;
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
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public long getAnswer() {
		return answer;
	}
	public void setAnswer(long answer) {
		this.answer = answer;
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
	
	
}
