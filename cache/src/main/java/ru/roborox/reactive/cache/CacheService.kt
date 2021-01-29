package ru.roborox.reactive.cache

import reactor.core.publisher.Mono

interface CacheService<K, V> {
    fun get(key: K): Mono<V>
}