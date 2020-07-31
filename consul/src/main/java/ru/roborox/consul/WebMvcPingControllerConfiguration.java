package ru.roborox.consul;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcPingControllerConfiguration {
    @RestController
    class PingController {
        @GetMapping({"/ping"})
        public String ping() {
            return "{\"success\":true}";
        }
    }
}
