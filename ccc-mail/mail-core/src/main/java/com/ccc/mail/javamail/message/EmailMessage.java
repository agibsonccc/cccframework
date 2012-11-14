package com.ccc.mail.javamail.message;

import java.io.InputStream;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

public class EmailMessage extends MimeMessage {

	public EmailMessage(Folder folder, InputStream is, int msgnum)
			throws MessagingException {
		super(folder, is, msgnum);
	}

	public EmailMessage(Folder folder, int msgnum) {
		super(folder, msgnum);
	}

	public EmailMessage(Folder folder, InternetHeaders headers, byte[] content,
			int msgnum) throws MessagingException {
		super(folder, headers, content, msgnum);
	}

	public EmailMessage(MimeMessage source) throws MessagingException {
		super(source);
	}

	public EmailMessage(Session session, InputStream is)
			throws MessagingException {
		super(session, is);
	}

	public EmailMessage(Session session) {
		super(session);
	}

	@Override
	public void setFrom(Address address) throws MessagingException {
		super.setFrom(address);
		
		
	}

	@Override
	public void addFrom(Address[] addresses) throws MessagingException {
		super.addFrom(addresses);
	
		
	}

	@Override
	public void setSubject(String subject) throws MessagingException {
		super.setSubject(subject);
	}

	@Override
	public void setText(String text) throws MessagingException {
		super.setText(text);
		this.textForBody=text;
	}

	@Override
	public void setText(String text, String charset) throws MessagingException {
		super.setText(text, charset);
		this.textForBody=text;

	}

	@Override
	public void setText(String text, String charset, String subtype)
			throws MessagingException {
		super.setText(text, charset, subtype);
		this.textForBody=text;

	}

	@Override
	public void addRecipient(javax.mail.Message.RecipientType type,
			Address address) throws MessagingException {
		super.addRecipient(type, address);
	}
	
	public boolean isFlagged() {
		return isFlagged;
	}

	public void setFlagged(boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	public String getTextForBody() {
		return textForBody;
	}

	public void setTextForBody(String textForBody) {
		this.textForBody = textForBody;
	}

	public String getFromAddsCSV() {
		return fromAddsCSV;
	}

	public void setFromAddsCSV(String fromAddsCSV) {
		this.fromAddsCSV = fromAddsCSV;
	}

	public String getToAddCSV() {
		return toAddCSV;
	}

	public void setToAddCSV(String toAddCSV) {
		this.toAddCSV = toAddCSV;
	}

	public boolean isHasAttachments() {
		return hasAttachments;
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	private boolean isFlagged;
	
	
	private String textForBody;
	
	private String fromAddsCSV;
	
	private String toAddCSV;
	
	private boolean hasAttachments;

}
