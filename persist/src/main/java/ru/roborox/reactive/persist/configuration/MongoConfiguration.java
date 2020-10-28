package ru.roborox.reactive.persist.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import ru.roborox.reactive.persist.converter.CustomConversionsFactory;
import ru.roborox.reactive.persist.jackson.ObjectIdCombinedSerializer;

@Configuration
@Import({
    ConvertersConfiguration.class,
    ObjectIdCombinedSerializer.class
})
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${mongoUrls}")
    private String mongoUrls;
    @Value("${mongoDatabase}")
    private String mongoDatabase;
    @Autowired
    private CustomConversionsFactory customConversionsFactory;

    @NotNull
    @Bean
    @Override
    public MongoClient reactiveMongoClient() {
        return super.reactiveMongoClient();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        logger.info("creating mongoClient using {}", mongoUrls);
        builder.applyConnectionString(new ConnectionString(String.format("mongodb://%s", mongoUrls)));
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    public MongoCustomConversions customConversions() {
        return customConversionsFactory.create();
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
