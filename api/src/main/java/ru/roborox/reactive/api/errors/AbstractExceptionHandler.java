package ru.roborox.reactive.api.errors;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class AbstractExceptionHandler {
    protected String buildMessage(ServerHttpRequest request, Throwable ex) {
        return "Failed to handle request [" + request.getMethodValue() + " "
            + request.getURI() + "]: " + ex.getMessage();
    }
}
