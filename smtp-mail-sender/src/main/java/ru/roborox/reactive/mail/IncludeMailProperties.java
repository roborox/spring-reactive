package ru.roborox.reactive.mail;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ru.roborox.reactive.mail.MailPropertiesConfiguration.class)
public @interface IncludeMailProperties {
}

@Configuration
@PropertySource(value = {"classpath:/mail.properties", "classpath:/mail-test.properties", "file:../conf/mail.properties"}, ignoreResourceNotFound = true)
class MailPropertiesConfiguration {
}
