package ru.roborox.reactive.mail;

import org.springframework.beans.factory.annotation.Value;

public class MailSenderConfig {
	@Value("${smtpHost}")
	private String smtpHost;
	@Value("${smtpPort}")
	private int smtpPort;
	@Value("${useTls}")
	private boolean useTls;
	@Value("${smtpUser}")
	private String smtpUser;
	@Value("${smtpPassword}")
	private String smtpPassword;
	@Value("${email}")
	private String email;
	@Value("${emailAlias}")
	private String emailAlias;

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public boolean isUseTls() {
		return useTls;
	}

	public void setUseTls(boolean useTls) {
		this.useTls = useTls;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailAlias() {
		return emailAlias;
	}

	public void setEmailAlias(String emailAlias) {
		this.emailAlias = emailAlias;
	}

	@Override
	public String toString() {
		return "MailSenderConfig{" +
				"smtpHost='" + smtpHost + '\'' +
				", smtpPort=" + smtpPort +
				", useTls=" + useTls +
				", smtpUser='" + smtpUser + '\'' +
				", smtpPassword='" + smtpPassword + '\'' +
				", email='" + email + '\'' +
				", emailAlias='" + emailAlias + '\'' +
				'}';
	}
}
