@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package ru.roborox.kotlin.scala

import reactor.core.publisher.Flux
import ru.roborox.scala.Lists

fun <T> java.util.Collection<T>.toScala() = Lists.toScala(this)
fun <T> scala.collection.immutable.List<T>.toJava() = Lists.toJava(this)
fun <T> scala.collection.immutable.List<T>.toFlux() = Flux.fromIterable(Lists.toJava(this))
