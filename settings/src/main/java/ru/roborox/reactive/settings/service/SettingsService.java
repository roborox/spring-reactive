package ru.roborox.reactive.settings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import ru.roborox.reactive.settings.Setting;

public class SettingsService {
    @Autowired
    private ReactiveMongoOperations mongo;

    public Mono<String> getSetting(String id) {
        return mongo.findById(id, Setting.class)
            .map(Setting::getValue);
    }

    public Mono<Void> setSetting(String id, String value) {
        return mongo.upsert(
            Query.query(Criteria.where("_id").is(id)),
            Update.update("value", value),
            Setting.class
        ).then();
    }
}
