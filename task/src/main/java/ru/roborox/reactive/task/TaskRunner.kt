package ru.roborox.reactive.task

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ru.roborox.kotlin.coroutine.optimisticLock

class TaskRunner(
    private val taskRepository: TaskRepository
) {
    @ExperimentalCoroutinesApi
    suspend fun <T: Any> run(param: String, handler: TaskHandler<T>) {
        val task = findAndMarkRunning(handler.type, param)
        if (task != null) {
            runAndSaveTask(task, param, handler)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T: Any> runAndSaveTask(task: Task, param: String, handler: TaskHandler<T>) {
        var current = task
        try {
            handler.run(task.state as T, param)
                .collect { next ->
                    current = taskRepository.save(current.withState(next)).awaitFirst()
                }
            taskRepository.save(current.markCompleted()).awaitFirst()
        } catch (e: Throwable) {
            taskRepository.save(current.markError(e))
        }
    }

    private suspend fun findAndMarkRunning(type: String, param: String): Task? {
        return optimisticLock {
            val task = taskRepository.findByTypeAndParam(type, param).awaitFirstOrNull()
            if (task != null) {
                if (!task.running) {
                    taskRepository.save(task.markRunning()).awaitFirst()
                } else {
                    null
                }
            } else {
                val newRunningTask = Task(
                    type = type,
                    param = param,
                    lastStatus = TaskStatus.NONE,
                    state = null,
                    running = true
                )
                taskRepository.save(newRunningTask).awaitFirst()
            }
        }
    }
}