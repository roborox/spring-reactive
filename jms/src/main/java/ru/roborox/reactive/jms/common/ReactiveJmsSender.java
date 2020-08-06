package ru.roborox.reactive.jms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.jms.Destination;

public class ReactiveJmsSender {
    public static final Logger logger = LoggerFactory.getLogger(ReactiveJmsSender.class);

    private final JmsTemplate template;

    public ReactiveJmsSender(JmsTemplate template) {
        this.template = template;
    }

    public Mono<Void> convertAndSend(Destination destination, Object message) {
        return Mono.just(message)
            .publishOn(Schedulers.elastic())
            .flatMap(msg -> {
                try {
                    if (logger.isDebugEnabled())
                        logger.debug("convertAndSend destination={} msg={}", destination, msg);
                    template.convertAndSend(destination, msg);
                    return Mono.empty();
                } catch (Throwable e) {
                    return Mono.error(e);
                }
            });
    }
}
