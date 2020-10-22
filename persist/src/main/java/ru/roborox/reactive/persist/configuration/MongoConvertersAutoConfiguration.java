package ru.roborox.reactive.persist.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.roborox.reactive.persist.converter.CustomMongoConverter;
import ru.roborox.reactive.persist.converter.ObjectNodeMongoConverter;

@Configuration
@ConditionalOnBean(ObjectMapper.class)
public class MongoConvertersAutoConfiguration {
    @Bean
    public CustomMongoConverter objectNodeMongoConverter(ObjectMapper mapper) {
        return new ObjectNodeMongoConverter(mapper);
    }
}
