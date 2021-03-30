package ru.roborox.reactive.task

import kotlinx.coroutines.flow.Flow

interface TaskHandler<T: Any> {
    val type: String
    fun run(resume: T?, param: String): Flow<T>
}