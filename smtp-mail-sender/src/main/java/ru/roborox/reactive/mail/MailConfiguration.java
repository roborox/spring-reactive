package ru.roborox.reactive.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfiguration {
    @Bean
    public ru.roborox.reactive.mail.MailSenderConfig mailSenderConfig() {
        return new ru.roborox.reactive.mail.MailSenderConfig();
    }

    @Bean
    public MailSender smtpMailSender() {
        return new ru.roborox.reactive.mail.SmtpMailSender(mailSenderConfig());
    }

    @Bean
    public ReactiveMailSender reactiveMailSender() {
        return new ReactiveMailSender(smtpMailSender());
    }
}
