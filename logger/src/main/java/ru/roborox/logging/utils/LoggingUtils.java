package ru.roborox.logging.utils;

import kotlin.Pair;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoggingUtils {
    public static final String PREFIX = "___log_";

    public static <T> Mono<T> withMarker(Function<LogstashMarker, Mono<T>> action) {
        return Mono.subscriberContext()
            .map(LoggingUtils::createMarker)
            .flatMap(action);
    }
    
    public static <T> Flux<T> withMarkerFlux(Function<LogstashMarker, Flux<T>> action) {
        return Mono.subscriberContext()
            .map(LoggingUtils::createMarker)
            .flatMapMany(action);
    }

    public static Mono<LogstashMarker> marker() {
        return Mono.subscriberContext()
            .map(LoggingUtils::createMarker);
    }

    private static LogstashMarker createMarker(Context ctx) {
        final Map<Object, Object> map = ctx.stream()
            .filter(it -> it.getKey() instanceof String && ((String) it.getKey()).startsWith(PREFIX))
            .collect(Collectors.toMap(e -> ((String) e.getKey()).substring(PREFIX.length()), Map.Entry::getValue));
        return Markers.appendEntries(map);
    }

    public static Context toContext(Map<String, String> loggerContext) {
        return Context.of(loggerContext.entrySet().stream()
            .map(e -> new Pair<>(PREFIX + e.getKey(), e.getValue()))
            .collect(Collectors.toMap(Pair<String, String>::component1, Pair<String, String>::component2)));
    }
}
