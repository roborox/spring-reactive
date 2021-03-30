package ru.roborox.reactive.task

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.apache.commons.lang3.RandomUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.FluxSink
import reactor.core.publisher.ReplayProcessor
import ru.roborox.reactive.test.wait.Wait.waitAssert
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
class TaskRunnerTest : AbstractIntegrationTest() {
    private val events: ReplayProcessor<Int> = ReplayProcessor.create<Int>()
    private val sink: FluxSink<Int> = events.sink()

    val handler = object : TaskHandler<Int> {
        override val type: String
            get() = "MOCK1"

        override fun runLongTask(resume: Int?, param: String): Flow<Int> {
            return events.asFlow()
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
                .isOfAnyClassIn(IllegalStateException::class.java)
            assertThat(finished)
                .isFalse()
        }


    }
}