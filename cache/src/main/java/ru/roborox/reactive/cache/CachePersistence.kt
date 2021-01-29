package ru.roborox.reactive.cache

import reactor.core.publisher.Mono

/**
 * Cache persistency. Should save, load data, update access dates, delete stale data
 */
interface CachePersistence<K, V> {
    /**
     * load data from persistent cache by key. should update access date
     */
    fun load(key: K): Mono<V>
    fun save(key: K, value: CacheResponse<V>): Mono<Void>
}