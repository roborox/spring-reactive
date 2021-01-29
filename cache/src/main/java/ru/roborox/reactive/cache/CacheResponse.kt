package ru.roborox.reactive.cache

data class CacheResponse<T>(val value: T, val ttl: Long)