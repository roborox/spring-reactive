package ru.roborox.reactive.task

import kotlinx.coroutines.flow.Flow

interface TaskHandler<T: Any> {
    val type: String
    fun runLongTask(from: T?, param: String): Flow<T>
}