package ru.roborox.reactive.cache

import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.roborox.reactive.lock.LockService

class CacheServiceImpl<K, V>(
    private val lockService: LockService,
    private val descriptor: CacheDescriptor<K, V>,
    private val persistence: CachePersistence<K, V>,
    private val lockMaxTimeMs: Int
) : CacheService<K, V> {

    override fun get(key: K): Mono<V> =
        persistence.load(key)
            .switchIfEmpty(lockAndGet(key))

    private fun lockAndGet(key: K): Mono<V> =
        lockService.synchronize(descriptor.keyToString(key), lockMaxTimeMs, loadAndSave(key))

    private fun loadAndSave(key: K): Mono<V> =
        persistence.load(key)
            .switchIfEmpty {
                descriptor.get(key)
                    .flatMap { persistence.save(key, it).thenReturn(it.value) }

            }
}