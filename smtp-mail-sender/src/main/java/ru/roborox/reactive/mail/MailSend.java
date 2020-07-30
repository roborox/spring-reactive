package ru.roborox.reactive.mail;

import org.apache.commons.mail.HtmlEmail;

public class MailSend {
    public static void main(String[] args) throws Exception {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("UTF-8");
        email.setHostName("localhost");
        email.setSmtpPort(1025);
        email.addTo("evgeny.nacu@gmail.com");
        email.setFrom("en@daonomic.io");
        email.setSubject("This is subject");
        email.setTextMsg("This is test");
        System.out.println(email.send());
    }
}
