package ru.roborox.reactive.lock

import reactor.core.publisher.Mono

class NoopLockService: LockService{
    override fun <T> synchronize(name: String, expiresMs: Int, op: Mono<T>): Mono<T> = op
}