package ru.roborox.reactive.exceptions;

import org.springframework.http.HttpStatus;

public class HttpStatusException extends RuntimeException {
    private final HttpStatus httpStatus;

    public HttpStatusException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatusException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatusException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
