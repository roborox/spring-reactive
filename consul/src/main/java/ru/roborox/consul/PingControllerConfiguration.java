package ru.roborox.consul;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication
public class PingControllerConfiguration {
    @RestController
    static class PingController {
        @GetMapping({"/ping"})
        public Mono<String> ping() {
            return Mono.just("{\"success\":true}");
        }
    }
}
