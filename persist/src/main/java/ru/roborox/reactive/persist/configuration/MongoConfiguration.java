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

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Autowired
    private CustomConversionsFactory customConversionsFactory;

    @Bean
    @Override
    @NotNull
    public MongoClient reactiveMongoClient() {
        return super.reactiveMongoClient();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        logger.info("creating mongoClient using {}", uri);
        builder.applyConnectionString(new ConnectionString(uri));
    }

    @Override
    @NotNull
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @NotNull
    public MongoCustomConversions customConversions() {
        return customConversionsFactory.create();
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
