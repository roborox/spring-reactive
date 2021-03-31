package ru.roborox.reactive.task

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val runner: TaskRunner,
    handlers: List<TaskHandler<*>>
) {
    val handlersMap = handlers.map { it.type to it }.toMap()

    init {
        GlobalScope.launch {
            taskRepository.findByRunning(true)
                .asFlow()
                .collect {
                    logger.info("marking as not running: ${it.type}:${it.param} $it")
                    taskRepository.save(it.clearRunning()).awaitFirst()
                }
        }
    }

    @Scheduled(initialDelayString = "\${roborox.task.initialDelay:60000}", fixedDelayString = "\${roborox.task.delay:60000}")
    fun readAndRun() {
        GlobalScope.launch {
            logger.info("readAndRun()")
            Flux.concat(
                taskRepository.findByRunningAndLastStatus(false, TaskStatus.ERROR),
                taskRepository.findByRunningAndLastStatus(false, TaskStatus.NONE)
            )
                .asFlow()
                .map {
                    runTask(it.type, it.param)
                }
                .collect {
                    logger.info("started: $it")
                }
            logger.info("completed readAndRun()")
        }
    }

    fun runTask(type: String, param: String) {
        GlobalScope.launch { //todo GlobalScope - not good
            logger.info("runTask type=$type param=$param")
            val handler = handlersMap.getValue(type)
            runner.runLongTask(param, handler)
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TaskService::class.java)
    }
}