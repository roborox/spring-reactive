package ru.roborox.reactive.settings.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.roborox.reactive.settings.service.SettingsService;

@Configuration
public class SettingsConfiguration {
    @Bean
    public SettingsService settingsService() {
        return new SettingsService();
    }
}
