package ru.roborox.reactive.task

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface TaskRepository : ReactiveCrudRepository<Task, String> {
    fun findByTypeAndParam(type: String, param: String): Mono<Task>
}