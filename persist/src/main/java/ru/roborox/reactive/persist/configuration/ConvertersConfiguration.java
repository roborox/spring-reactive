package ru.roborox.reactive.persist.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.roborox.reactive.persist.converter.BigDecimalCustomMongoConverter;
import ru.roborox.reactive.persist.converter.BigIntegerCustomMongoConverter;
import ru.roborox.reactive.persist.converter.CustomConversionsFactory;
import ru.roborox.reactive.persist.converter.CustomMongoConverter;

import java.util.List;

@Configuration
public class ConvertersConfiguration {
    @Bean
    public CustomMongoConverter bigIntegerCustomMongoConverter() {
        return new BigIntegerCustomMongoConverter();
    }

    @Bean
    public CustomMongoConverter bigDecimalCustomMongoConverter() {
        return new BigDecimalCustomMongoConverter();
    }

    @Bean
    public CustomConversionsFactory customConversionsFactory(List<CustomMongoConverter> customMongoConverters) {
        return new CustomConversionsFactory(customMongoConverters);
    }
}
