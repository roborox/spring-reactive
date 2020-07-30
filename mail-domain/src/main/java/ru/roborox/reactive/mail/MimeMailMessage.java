package ru.roborox.reactive.mail;

import java.io.Serializable;
import java.util.List;

public class MimeMailMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> from;
	private List<String> recipients;
	private String subject;
	private String contentType;
	private byte[] rawContent;
	
	public MimeMailMessage() {
	}
	
	public MimeMailMessage(List<String> from, List<String> recipients, String subject, String contentType,
			byte[] rawContent) {
		this.from = from;
		this.recipients = recipients;
		this.subject = subject;
		this.contentType = contentType;
		this.rawContent = rawContent;
	}

	public List<String> getFrom() {
		return from;
	}

	public void setFrom(List<String> from) {
		this.from = from;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getRawContent() {
		return rawContent;
	}

	public void setRawContent(byte[] rawContent) {
		this.rawContent = rawContent;
	}

	@Override
	public String toString() {
		return "MimeMailMessage [from=" + from + ", recipients=" + recipients + ", subject=" + subject + "]";
	}
}
