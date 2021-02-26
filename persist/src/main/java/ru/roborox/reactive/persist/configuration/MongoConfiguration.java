package ru.roborox.reactive.persist.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import ru.roborox.reactive.persist.converter.CustomConversionsFactory;
import ru.roborox.reactive.persist.jackson.ObjectIdCombinedSerializer;

@Configuration
@Import({
    ConvertersConfiguration.class,
    ObjectIdCombinedSerializer.class
})
public class MongoConfiguration {
    @Autowired
    private CustomConversionsFactory customConversionsFactory;

    @Bean
    public MongoCustomConversions customConversions() {
        return customConversionsFactory.create();
    }
}
