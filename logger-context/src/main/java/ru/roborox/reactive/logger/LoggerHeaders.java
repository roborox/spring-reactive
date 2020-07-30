package ru.roborox.reactive.logger;

import kotlin.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.*;
import java.util.function.Function;

public class LoggerHeaders {
    private final Map<String, Pair<String, Function<ServerHttpRequest, Optional<String>>>> headerToAttribute = new HashMap<>();
    private final Map<String, String> attributeToHeader = new HashMap<>();

    public LoggerHeaders() {
        register(Headers.ACTION_ID, "action", it -> Optional.of(UUID.randomUUID().toString().replace("-", "")));
        register(Headers.ADV_ID, "advId");
        register(Headers.PLATFORM, "platform");
        register(Headers.APP_VERSION, "appVersion");
        register(Headers.REQUEST, "request", req -> Optional.of(req.getMethod() + " " + req.getURI()));
    }

    public void register(String headerName, String attributeName) {
        register(headerName, attributeName, it -> Optional.empty());
    }

    public void register(String headerName, String attributeName, Function<ServerHttpRequest, Optional<String>> defaultValue) {
        headerToAttribute.put(headerName, new Pair<>(attributeName, defaultValue));
        attributeToHeader.put(attributeName, headerName);
    }

    public Map<String, String> getLoggerContext(ServerHttpRequest request) {
        final HttpHeaders headers = request.getHeaders();
        HashMap<String, String> attrs = new HashMap<>();
        headerToAttribute.forEach((k, v) -> {
            final String paramName = v.component1();
            List<String> headerValue = headers.get(k);
            if (headerValue != null && !headerValue.isEmpty()) {
                attrs.put(paramName, headerValue.get(0));
            } else {
                final Optional<String> defaultValue = v.component2().apply(request);
                if (defaultValue.isPresent()) {
                    attrs.put(paramName, defaultValue.get());
                }
            }
        });
        return attrs;
    }

/*
    public <S extends WebClient.RequestHeadersSpec<S>> Mono<S> addHeaders(S request) {
        return ReactiveLoggerContext.loggerContext()
            .map(context -> {
                S result = request;
                for (ru.roborox.reactive.logger.Pair<String, String> attr : context.getValues()) {
                    String headerName = attributeToHeader.get(attr.getKey());
                    if (headerName != null) {
                        result = result.header(headerName, attr.getValue());
                    }
                }
                return result;
            });
    }
*/

}
