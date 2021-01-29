package ru.roborox.reactive.cache

import reactor.core.publisher.Mono

/**
 * Describes how to load data and how to serialize keys to strings
 */
interface CacheDescriptor<K, V> {
    /**
     * serializes key to string (locks are identified by strings)
     */
    fun keyToString(key: K): String

    /**
     * real get, fetches data by key
     */
    fun get(key: K): Mono<CacheResponse<V>>
}