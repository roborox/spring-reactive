package ru.roborox.reactive.persist.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.roborox.reactive.persist.converter.*;

import java.util.List;

@Configuration
public class ConvertersConfiguration {
    @Autowired
    private ObjectMapper mapper;

    @Bean
    public CustomMongoConverter objectNodeMongoConverter() {
        return new ObjectNodeMongoConverter(mapper);
    }

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
