package ru.roborox.reactive.cache

import reactor.core.publisher.Mono

interface CacheDescriptor<T: Any> {
    val collection: String

    fun getMaxAge(value: T?): Long

    fun get(id: String): Mono<T>
}