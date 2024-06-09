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
	}

	public void setMailType(String mailType, String encType) {
		this.mailType = mailType;
		this.encType = encType;
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			String username = "email";
			String password = "password";
			return new PasswordAuthentication(username, password);
		}
	}

	public boolean mailSend(Mail dto) {
		boolean b = false;

		Properties p = new Properties();
		p.put("mail.smtp.user", "email");
		
		String host = "smtp.gmail.com";
		p.put("mail.smtp.host", host);

		p.put("mail.smtp.port", "465");
		p.put("mail.smtp.starttls.enable", "true");
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.socketFactory.port", "465");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");

		p.put("mail.smtp.ssl.enable", "true");
		p.put("mail.smtp.ssl.trust", host);
		
		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(p, auth);

			Message msg = new MimeMessage(session);
			
			if (dto.getSenderName() == null || dto.getSenderName().equals("")) {
				msg.setFrom(new InternetAddress(dto.getSenderEmail()));
			} else {
				msg.setFrom(new InternetAddress(dto.getSenderEmail(), dto.getSenderName(), encType));
			}

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getReceiverEmail()));
			msg.setHeader("Content-Type", mailType);
			msg.setHeader("X-Mailer", dto.getSenderName());
			msg.setSentDate(new Date());
			msg.setSubject(dto.getSubject());
			msg.setContent(dto.getContent(), "text/html; charset=utf-8");
			Transport.send(msg);

			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}
}
