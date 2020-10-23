package ru.roborox.reactive.api.errors

data class ErrorResponse (
    val fieldErrors: Map<String, List<String>>,
    val genericErrors: List<String>
)