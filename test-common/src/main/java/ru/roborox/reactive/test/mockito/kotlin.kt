package ru.roborox.reactive.test.mockito

import org.mockito.Mockito

fun <T> eq(t: T): T {
    Mockito.eq(t)
    return uninitialized()
}

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

fun <T> same(t: T): T {
    Mockito.same(t)
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T