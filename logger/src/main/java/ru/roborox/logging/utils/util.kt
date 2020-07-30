package ru.roborox.logging.utils

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import ru.roborox.logging.utils.LoggingUtils.PREFIX

fun <T> Mono<T>.loggerContext(key: String, value: String): Mono<T> =
    subscriberContext(Context.of("$PREFIX$key", value))

fun <T> Flux<T>.loggerContext(key: String, value: String): Flux<T> =
    subscriberContext(Context.of("$PREFIX$key", value))

fun <T> Mono<T>.loggerContext(map: Map<String, String>): Mono<T> =
    subscriberContext(LoggingUtils.toContext(map))

fun <T> Flux<T>.loggerContext(map: Map<String, String>): Flux<T> =
    subscriberContext(LoggingUtils.toContext(map))