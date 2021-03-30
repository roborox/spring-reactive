package ru.roborox.reactive.task

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.roborox.kotlin.coroutine.optimisticLock

//todo log with context
@Service
class TaskRunner(
    private val taskRepository: TaskRepository
) {
    @ExperimentalCoroutinesApi
    suspend fun <T : Any> runLongTask(param: String, handler: TaskHandler<T>) {
        logger.info("running ${handler.type} with param=$param")
        val task = findAndMarkRunning(handler.type, param)
        if (task != null) {
            runAndSaveTask(task, param, handler)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> runAndSaveTask(task: Task, param: String, handler: TaskHandler<T>) {
        logger.info("starting task $task")
        var current = task
        try {
            handler.runLongTask(task.state as T?, param)
                .collect { next ->
                    logger.info("new task state for ${handler.type} with param=$param: $next")
                    current = taskRepository.save(current.withState(next)).awaitFirst()
                }
            logger.info("completed ${handler.type} with param=$param")
            taskRepository.save(current.markCompleted()).awaitFirst()
        } catch (e: Throwable) {
            logger.info("error caught executing ${handler.type} with param=$param", e)
            taskRepository.save(current.markError(e)).awaitFirst()
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

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TaskRunner::class.java)
    }
}