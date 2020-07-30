package ru.roborox.kotlin

import reactor.core.publisher.Mono
import scala.Option

fun <T> Mono<Option<T>>.fromOption(): Mono<T> =
    this.flatMap {
        if (it.isDefined) {
            Mono.justOrEmpty(it.get())
        } else {
            Mono.empty()
        }
    }
