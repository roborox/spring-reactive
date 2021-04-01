package ru.roborox.reactive.task

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.FluxSink
import reactor.core.publisher.ReplayProcessor
import ru.roborox.reactive.test.wait.Wait.waitAssert

@ExperimentalCoroutinesApi
class TaskRunnerTest : AbstractIntegrationTest() {
    private val events: ReplayProcessor<Int> = ReplayProcessor.create<Int>()
    private val sink: FluxSink<Int> = events.sink()

    val handler = object : TaskHandler<Int> {
        override val type: String
            get() = "MOCK1"

        override fun runLongTask(from: Int?, param: String): Flow<Int> {
            return events.asFlow()
        }
    }

    val handlerUnableToRun = object : TaskHandler<Int> {
        override val type: String
            get() = "MOCK2"

        override suspend fun isAbleToRun(param: String): Boolean = false

        override fun runLongTask(from: Int?, param: String): Flow<Int> {
            return events.asFlow()
        }
    }

    @Test
    fun doesnRunIfUnable() = runBlocking<Unit> {
        runner.runLongTask("", handlerUnableToRun)

        val task = taskRepository.findByTypeAndParam("MOCK2", "").awaitFirst()
        assertThat(task)
            .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.NONE)
        assertThat(task)
            .hasFieldOrPropertyWithValue(Task::running.name, false)
    }

    @Test
    fun doesntRunCompleted() {
        val newTask = Task(
            type = "MOCK1",
            param = "p1",
            lastStatus = TaskStatus.COMPLETED,
            state = null,
            running = false
        )

        mongo.save(newTask).block()

        GlobalScope.launch {
            runner.runLongTask("p1", handler)
        }

        Thread.sleep(500)

        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.COMPLETED)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, false)
        }
    }

    @Test
    fun savesTheState() {
        var finished = false

        GlobalScope.launch {
            runner.runLongTask("p1", handler)
            finished = true
        }

        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.NONE)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, true)
            assertThat(runnerEvents)
                .hasSize(1)
        }

        val state1 = RandomUtils.nextInt()
        sink.next(state1)
        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::state.name, state1)
        }

        val state2 = RandomUtils.nextInt()
        sink.next(state2)
        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::state.name, state2)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, true)
            assertThat(runnerEvents)
                .hasSize(1)
        }

        sink.complete()
        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::state.name, state2)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, false)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.COMPLETED)
            assertThat(finished)
                .isTrue()
            assertThat(runnerEvents)
                .hasSize(2)
        }
    }

    @Test
    fun savesError() {
        var finished = false
        var error: Throwable? = null

        GlobalScope.launch {
            try {
                runner.runLongTask("p1", handler)
                finished = true
            } catch (e: Throwable) {
                error = e
            }
        }

        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.NONE)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, true)
        }

        sink.error(IllegalStateException())

        waitAssert {
            val found = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::state.name, null)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::running.name, false)
            assertThat(found)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.ERROR)
            assertThat(error)
                .isNull()
            assertThat(finished)
                .isTrue()
        }
    }
}