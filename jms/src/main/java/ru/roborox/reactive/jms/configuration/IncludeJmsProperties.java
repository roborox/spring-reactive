package ru.roborox.reactive.jms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JmsPropertiesConfiguration.class)
public @interface IncludeJmsProperties {
}

@Configuration
@PropertySource(value = {"classpath:/jms.properties", "classpath:/jms-test.properties", "file:../conf/jms.properties"}, ignoreResourceNotFound = true)
class JmsPropertiesConfiguration {
}
