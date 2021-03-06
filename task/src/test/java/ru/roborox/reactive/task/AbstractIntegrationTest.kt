package ru.roborox.reactive.task

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.test.context.ContextConfiguration
import ru.roborox.reactive.test.persist.MongoCleanup

@MongoCleanup
@SpringBootTest
@ContextConfiguration(classes = [MockContext::class])
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var mongo: ReactiveMongoOperations

    @Autowired
    protected lateinit var runner: TaskRunner

    @Autowired
    protected lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var listener: TaskRunnerEventListener

    @BeforeEach
    fun before() {
        listener.runnerEvents.clear()
    }

    val runnerEvents: List<TaskRunnerEvent>
        get() = listener.runnerEvents
}