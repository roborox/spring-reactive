package ru.roborox.reactive.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebFilter;

@Configuration
public class LoggerContextConfiguration {
    @Bean
    public LoggerHeaders loggerHeaders() {
        return new LoggerHeaders();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public WebFilter loggerContextFilter() {
        return new LoggerContextFilter(loggerHeaders());
    }
}
