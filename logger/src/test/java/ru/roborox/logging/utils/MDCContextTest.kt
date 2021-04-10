package ru.roborox.logging.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

class MDCContextTest {
    @Test
    fun test1() {
        coroutineToMono {
            val ctx = coroutineContext[MDCContext]
            println(ctx?.contextMap)
        }
            .loggerContext("key1", "value1")
            .block()
    }

    @Test
    fun test2() = runBlocking<Unit> {
        withContext(MDCContext(mapOf("key1" to "value1"))) {
            logger.info("this is log with MDC")
            LoggingUtils.withMarker {
                logger.info(it, "using Mono")
                Mono.just("value")
            }
                .awaitWithMdcContext()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MDCContextTest::class.java)
    }
}

private suspend fun <T> Mono<T>.awaitWithMdcContext(): T {
    return awaitWithContext { coroutineContext, context ->
        val mdc = coroutineContext[MDCContext]
        if (mdc != null) {
            context.put(MDC_CONTEXT, mdc)
        } else {
            context
        }
    }
}

private suspend fun <T> Mono<T>.awaitWithContext(contextHandler: (CoroutineContext, Context) -> Context): T {
    val ctx = coroutineContext
    return this
        .subscriberContext { contextHandler(ctx, it) }
        .awaitFirst()
}

fun <T> coroutineToMono(func: suspend CoroutineScope.() -> T?): Mono<T> {
    return Mono.subscriberContext().flatMap { ctx ->
        mono(ctx.toCoroutineContext(), func)
    }
}

fun <T> coroutineToFlux(func: suspend CoroutineScope.() -> Iterable<T>): Flux<T> {
    return Mono.subscriberContext().flatMap { ctx ->
        mono(ctx.toCoroutineContext(), func)
    }.flatMapIterable { it }
}

fun Context.toCoroutineContext(): CoroutineContext {
    return this.stream()
        .filter { it.value is CoroutineContext }
        .map { it.value as CoroutineContext }
        .reduce { context1, context2 -> context1 + context2 }
        .orElse(EmptyCoroutineContext)
}
