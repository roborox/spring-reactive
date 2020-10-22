package ru.roborox.reactive.lock

import reactor.core.publisher.Mono
import ru.roborox.kotlin.blockingToMono
import java.util.*
import java.util.concurrent.Callable

class BlockingLockService : LockService {
    override fun <T> synchronize(name: String, expiresMs: Int, op: Mono<T>): Mono<T> {
        return Mono.subscriberContext().flatMap { ctx ->
            Callable {
                val result = synchronized(name.intern()) {
                    op.subscriberContext(ctx).block()
                }
                Optional.ofNullable(result)
            }.blockingToMono().flatMap {
                if (it.isPresent) {
                    Mono.just(it.get())
                } else {
                    Mono.empty()
                }
            }
        }
    }
}