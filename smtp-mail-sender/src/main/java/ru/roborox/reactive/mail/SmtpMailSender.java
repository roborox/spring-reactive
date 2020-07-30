package ru.roborox.reactive.mail;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.util.Arrays;

public class SmtpMailSender implements MailSender {
    public static final Logger logger = LoggerFactory.getLogger(ru.roborox.reactive.mail.SmtpMailSender.class);

    private static final String SMTP_API_HEADER = "X-SMTPAPI";

    private final MailSenderConfig mailSenderConfig;

    public SmtpMailSender(MailSenderConfig mailSenderConfig) {
        this.mailSenderConfig = mailSenderConfig;
    }

    @Override
    public void send(MailMessage message) {
        try {
            String sender = message.getFrom();
            String senderAlias = message.getFromAlias();
            if (!StringUtils.isNotEmpty(sender)) {
                sender = mailSenderConfig.getEmail();
            }
            if (!StringUtils.isNoneEmpty(senderAlias)) {
                senderAlias = mailSenderConfig.getEmailAlias();
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(message.getTo())) {
                logger.info("Recipient is null ignoring message \"{}\"", message.getSubject());
                return;
            }
            HtmlEmail email = new HtmlEmail();
            if (message.isDisableRewriteUrl()) {
                email.addHeader(SMTP_API_HEADER, "{\"filters\" : {\"clicktrack\" : {\"settings\" : {\"enable\" : 0}}}}");
            }
            email.setStartTLSEnabled(mailSenderConfig.isUseTls());
            email.setHostName(mailSenderConfig.getSmtpHost());
            email.setSmtpPort(mailSenderConfig.getSmtpPort());
            email.setAuthentication(mailSenderConfig.getSmtpUser(), mailSenderConfig.getSmtpPassword());
            InternetAddress[] addresses = InternetAddress.parse(message.getTo(), false);
            for (InternetAddress address : addresses) {
                email.addTo(address.getAddress());
            }
            email.setFrom(sender, senderAlias);
            email.setSubject(message.getSubject());
            if (StringUtils.isNotEmpty(message.getText())) {
                email.setHtmlMsg(message.getText());
            }
            email.setCharset("UTF-8");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(message.getCc())) {
                email.setCc(Arrays.asList(InternetAddress.parse(message.getCc())));
            }
            if (message.getAttachments() != null) {
                for (Attachment attachment : message.getAttachments()) {
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getType());
                    if (StringUtils.isNotEmpty(attachment.getCid())) {
                        email.embed(dataSource, attachment.getName(), attachment.getCid());
                    } else {
                        email.attach(dataSource, attachment.getName(), "", EmailAttachment.ATTACHMENT);
                    }
                }
            }
            logger.info("sending email to {}", Arrays.toString(addresses));
            email.send();
        } catch (AddressException | EmailException e) {
            logger.error("unable to send mail", e);
            throw new RuntimeException(e);
        }
    }
}
