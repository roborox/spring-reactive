package ru.roborox.reactive.task

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import ru.roborox.reactive.test.wait.Wait.waitAssert
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
class TaskServiceTest : AbstractIntegrationTest() {
    @Autowired
    @Qualifier("mockHandler1")
    private lateinit var handler1: MockHandler
    @Autowired
    @Qualifier("mockHandler2")
    private lateinit var handler2: MockHandler
    @Autowired
    private lateinit var service: TaskService

    @Test
    fun executesInParallel() {
        runBlocking {
            taskRepository.save(Task(
                type = "MOCK1",
                param = "p1",
                lastStatus = TaskStatus.NONE,
                state = null,
                running = false
            )).awaitFirst()
            taskRepository.save(Task(
                type = "MOCK2",
                param = "p2",
                lastStatus = TaskStatus.NONE,
                state = null,
                running = false
            )).awaitFirst()
        }

        runBlocking {
            service.readAndRun()
        }

        waitAssert {
            val t1 = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            Assertions.assertThat(t1)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.NONE)
            Assertions.assertThat(t1)
                .hasFieldOrPropertyWithValue(Task::running.name, true)

            val t2 = taskRepository.findByTypeAndParam("MOCK2", "p2").block()!!
            Assertions.assertThat(t2)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.NONE)
            Assertions.assertThat(t2)
                .hasFieldOrPropertyWithValue(Task::running.name, true)
        }

        val v1 = RandomUtils.nextInt()
        handler1.sink.next(v1)
        val v2 = RandomUtils.nextInt()
        handler2.sink.next(v2)

        waitAssert {
            val t1 = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            Assertions.assertThat(t1)
                .hasFieldOrPropertyWithValue(Task::state.name, v1)

            val t2 = taskRepository.findByTypeAndParam("MOCK2", "p2").block()!!
            Assertions.assertThat(t2)
                .hasFieldOrPropertyWithValue(Task::state.name, v2)
        }

        handler1.sink.complete()
        handler2.sink.error(IllegalStateException())

        waitAssert {
            val t1 = taskRepository.findByTypeAndParam("MOCK1", "p1").block()!!
            Assertions.assertThat(t1)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.COMPLETED)

            val t2 = taskRepository.findByTypeAndParam("MOCK2", "p2").block()!!
            Assertions.assertThat(t2)
                .hasFieldOrPropertyWithValue(Task::lastStatus.name, TaskStatus.ERROR)
        }
    }
}