package ru.roborox.reactive.utils;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

public class MonoUtils {
    public static <T> Mono<T> tryMono(Callable<T> callable) {
        try {
            return Mono.just(callable.call());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
