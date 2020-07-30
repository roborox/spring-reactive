package ru.roborox.kotlin

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

fun <K, V> Map<K, V>.toMultiValueMap(): MultiValueMap<K, V> {
    val result = LinkedMultiValueMap<K, V>()
    for (entry in entries) {
        result[entry.key] = listOf(entry.value)
    }
    return result
}