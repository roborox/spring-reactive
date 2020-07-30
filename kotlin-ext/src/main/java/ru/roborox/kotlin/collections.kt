@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package ru.roborox.kotlin

import reactor.core.publisher.Flux
import java.util.*

fun <T> java.util.Collection<T>.toFlux() = Flux.fromIterable(this)
fun <T> Optional<T>.toList() = if (this.isPresent) listOf(this.get()) else emptyList()