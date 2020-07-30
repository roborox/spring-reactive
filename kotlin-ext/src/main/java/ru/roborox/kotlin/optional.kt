package ru.roborox.kotlin

import java.util.*

fun <T> Optional<T>.orNull(): T? = this.orElse(null)