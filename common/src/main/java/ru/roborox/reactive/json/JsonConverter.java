package ru.roborox.reactive.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import ru.roborox.reactive.utils.MonoUtils;

public class JsonConverter {
    private final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<String> toJson(Object object) {
        return MonoUtils.tryMono(() -> objectMapper.writeValueAsString(object));
    }

    public <T> Mono<T> fromJson(String json, Class<T> tClass) {
        return MonoUtils.tryMono(() -> objectMapper.readValue(json, tClass));
    }
}
