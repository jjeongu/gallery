package com.gallery.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	private String mailType; // 메일 타입
	private String encType;

	public MailSender() {
		this.encType = "utf-8";
		this.mailType = "text/html; charset=utf-8";
		// this.mailType = "text/plain; charset=utf-8";
	}

	public void setMailType(String mailType, String encType) {
		this.mailType = mailType;
		this.encType = encType;
	}

	// 네이버를 이용하여 메일을 보내는 경우 보내는사람의 이메일이 아래 계정(SMTP 권한 계정)과 다르면 메일 전송이 안된다.
	// gmail은 기본적으로 <a href ...> 태그가 있으면 href를 제거한다.
	// SMTP 권한
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			// gmail : 프로필(자기이름) 클릭 => 구글계정관리 버튼 클릭 => 좌측화면 보안 => 2단계인증 설정 후
			//       2단계인증 => 앱비밀번호 => 앱이름입력 => 생성
			//       패스워드 대신 2단계 인증 앱비밀번호 입력
			//       앱비밀번호가 없으면 검색
			// 네이버 : 메일 아래부분 환경설정 클릭후 POP3등을 허용
            //       POP3/SMTP, IMAP/SMTP 사용함 설정(나머지는 기본 설정)
			
			// String username = "아이디@naver.com"; // 네이버 사용자;
			String username = "이메일"; // gmail 사용자;
			String password = "패스워드"; // 패스워드;
			return new PasswordAuthentication(username, password);
		}
	}

	public boolean mailSend(Mail dto) {
		boolean b = false;

		Properties p = new Properties();

		// SMTP 서버의 계정 설정
		// Naver와 연결할 경우 네이버 아이디
		// Gmail과 연결할 경우 Gmail 아이디
		p.put("mail.smtp.user", "이메일");

		// SMTP 서버 정보 설정
		// String host = "smtp.naver.com"; // 네이버
		String host = "smtp.gmail.com"; // gmail
		
		p.put("mail.smtp.host", host);

		// 네이버와 지메일 동일
		p.put("mail.smtp.port", "465");
		p.put("mail.smtp.starttls.enable", "true");
		p.put("mail.smtp.auth", "true");
		// p.put("mail.smtp.debug", "true");
		p.put("mail.smtp.socketFactory.port", "465");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");

		p.put("mail.smtp.ssl.enable", "true");
		p.put("mail.smtp.ssl.trust", host);
		
		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(p, auth);
			// 메일 전송시 상세 정보 콘솔에 출력 여부
			// session.setDebug(true);

			Message msg = new MimeMessage(session);

			// 보내는 사람
			if (dto.getSenderName() == null || dto.getSenderName().equals("")) {
				msg.setFrom(new InternetAddress(dto.getSenderEmail()));
			} else {
				msg.setFrom(new InternetAddress(dto.getSenderEmail(), dto.getSenderName(), encType));
			}

			// 받는 사람
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getReceiverEmail()));

			// 메일 타입
			msg.setHeader("Content-Type", mailType);
			
			// 보내는 사람 이름
			msg.setHeader("X-Mailer", dto.getSenderName());
			
			// 메일 보낸 날짜
			msg.setSentDate(new Date());

			// 제목
			msg.setSubject(dto.getSubject());
			
			// 내용
			// msg.setText(dto.getContent());
			msg.setContent(dto.getContent(), "text/html; charset=utf-8");
			// 메일 전송
			Transport.send(msg);

			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}
}
