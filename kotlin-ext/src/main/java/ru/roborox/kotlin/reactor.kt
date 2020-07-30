package ru.roborox.kotlin

import org.slf4j.Logger
import org.springframework.dao.OptimisticLockingFailureException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.function.Tuple2
import reactor.util.function.Tuple3
import reactor.util.function.Tuple4
import reactor.util.function.Tuple5
import java.util.*
import java.util.concurrent.Callable

interface Extended<T> {
    val base: T
}

fun <T, R : Extended<T>> Flux<T>.extend(mapper: (T) -> Mono<R>): Mono<List<R>> {
    return this.collectList()
        .flatMap { it.extend(mapper) }
}

fun <T, R : Extended<T>> List<T>.extend(mapper: (T) -> Mono<R>): Mono<List<R>> {
    return Flux.fromIterable(this)
        .flatMap(mapper)
        .collectList()
        .map { result ->
            val map = result.map { it.base to it }.toMap()
            this.map { map.getValue(it) }
        }
}

interface Identifiable<Id> {
    val id: Id
}

fun <Id, T: Identifiable<Id>, R: Identifiable<Id>> Flux<T>.extendById(mapper: (T) -> Mono<R>): Mono<List<R>> {
    return this.collectList()
        .flatMap { it.extendById(mapper) }
}

fun <Id, T: Identifiable<Id>, R: Identifiable<Id>> List<T>.extendById(mapper: (T) -> Mono<R>): Mono<List<R>> {
    return Flux.fromIterable(this)
        .flatMap(mapper)
        .collectList()
        .map { result ->
            val map = result.map { it.id to it }.toMap()
            this.map { map.getValue(it.id) }
        }
}


fun <T> Mono<T>.retryOptimistickLock(retries: Int = 5): Mono<T> = this
    .retry(5) {
        it is OptimisticLockingFailureException
    }


inline fun <reified R> Flux<*>.filterIsInstance(): Flux<R> {
    return this
        .filter { it is R }
        .map { it as R }
}

fun <T, R> Flux<T>.mapNotNull(mapper: (T) -> R?): Flux<R> {
    return this
        .flatMap {
            val r = mapper(it)
            if (r == null) {
                Mono.empty()
            } else {
                Mono.just(r)
            }
        }
}

fun <T> Mono<T>.toOptional(): Mono<Optional<T>> =
    this.map { Optional.of(it) }
        .switchIfEmpty(Mono.just(Optional.empty()))

fun <T> Mono<Optional<T>>.fromOptional(): Mono<T> =
    this.flatMap {
        if (it.isPresent) {
            Mono.just(it.get())
        } else {
            Mono.empty()
        }
    }

fun <T> Mono<Collection<T>>.toFlux(): Flux<T> =
    this.flatMapMany { Flux.fromIterable(it) }

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 {
    return t1
}

operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 {
    return t2
}

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component1(): T1 {
    return t1
}

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component2(): T2 {
    return t2
}

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component3(): T3 {
    return t3
}

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component1(): T1 {
    return t1
}

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component2(): T2 {
    return t2
}

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component3(): T3 {
    return t3
}

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component4(): T4 {
    return t4
}

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component1(): T1 {
    return t1
}

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component2(): T2 {
    return t2
}

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component3(): T3 {
    return t3
}

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component4(): T4 {
    return t4
}

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component5(): T5 {
    return t5
}

fun <T> Mono<T>.subscribeAndLog(name: String, logger: Logger) {
    this.subscribe(
        {},
        { logger.error("error caught while: $name", it) },
        { logger.info("completed successfully: $name") },
        { logger.info("started: $name") }
    )
}

fun <T> T?.justOrEmpty(): Mono<T> {
    return Mono.justOrEmpty(this)
}

fun <T> Callable<T>.blockingToMono(): Mono<T> {
    return Mono.just(Unit)
        .publishOn(Schedulers.elastic())
        .flatMap {
            try {
                Mono.just(this.call())
            } catch (e: Exception) {
                Mono.error<T>(e)
            }
        }
}
