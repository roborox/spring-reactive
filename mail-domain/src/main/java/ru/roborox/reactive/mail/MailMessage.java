package ru.roborox.reactive.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class MailMessage {
	private String to;
	private String from;
	private String fromAlias;
	private String cc;
	private String subject;
	private String text;
	private List<Attachment> attachments;
	private boolean disableRewriteUrl;
	private ObjectNode smtpApiHeader;

	public MailMessage from(String from) {
		setFrom(from);
		return this;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public MailMessage fromAlias(String fromAlias) {
		setFromAlias(fromAlias);
		return this;
	}

	public String getFromAlias() {
		return fromAlias;
	}

	public void setFromAlias(String fromAlias) {
		this.fromAlias = fromAlias;
	}

	public MailMessage subject(String subject) {
		setSubject(subject);
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MailMessage text(String text) {
		setText(text);
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public MailMessage attachments(List<Attachment> attachments) {
		setAttachments(attachments);
		return this;
	}

	public MailMessage attachments(Attachment... attachments) {
		return attachments(asList(attachments));
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public MailMessage to(String to) {
		setTo(to);
		return this;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public MailMessage cc(String cc) {
		setCc(cc);
		return this;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public MailMessage smtpApiHeader(ObjectNode smtpApiHeader) {
		setSmtpApiHeader(smtpApiHeader);
		return this;
	}


	public ObjectNode getSmtpApiHeader() {
		return smtpApiHeader;
	}

	public void setSmtpApiHeader(ObjectNode smtpApiHeader) {
		this.smtpApiHeader = smtpApiHeader;
	}

	@JsonIgnore
	public ObjectNode getCalculatedSmtpApiHeader() {
		if (disableRewriteUrl) {
			disableRewriteUrl();
			return getSmtpApiHeader();
		} else {
			return getSmtpApiHeader();
		}
	}

	@Deprecated
	public boolean isDisableRewriteUrl() {
		return disableRewriteUrl;
	}

	@Deprecated
	public void setDisableRewriteUrl(boolean disableRewriteUrl) {
		this.disableRewriteUrl = disableRewriteUrl;
	}

	public MailMessage disableRewriteUrl() {
		initHeader()
			.with("filters")
			.with("clicktrack")
			.with("settings")
			.put("enable", 0);
		return this;
	}

	public MailMessage category(String category) {
		initHeader()
			.put("category", category);
		return this;
	}

	public MailMessage categories(String... categories) {
		initHeader()
			.putArray("category")
			.addAll(Stream.of(categories).map(JsonNodeFactory.instance::textNode).collect(Collectors.toList()));
		return this;
	}

	public MailMessage categories(List<String> categories) {
		initHeader()
			.putArray("category")
			.addAll(categories.stream().map(JsonNodeFactory.instance::textNode).collect(Collectors.toList()));
		return this;
	}

	public MailMessage uniqueArg(String key, String value) {
		initHeader()
			.with("unique_args")
			.put(key, value);
		return this;
	}

	private ObjectNode initHeader() {
		if (smtpApiHeader == null) {
			smtpApiHeader = JsonNodeFactory.instance.objectNode();
		}
		return smtpApiHeader;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailMessage other = (MailMessage) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}


}
