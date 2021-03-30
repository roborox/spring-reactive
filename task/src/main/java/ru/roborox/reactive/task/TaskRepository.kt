package ru.roborox.reactive.task

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TaskRepository : ReactiveCrudRepository<Task, String> {
    fun findByTypeAndParam(type: String, param: String): Mono<Task>
    fun findByRunning(running: Boolean): Flux<Task>
    fun findByRunningAndLastStatus(running: Boolean, lastStatus: TaskStatus): Flux<Task>
}