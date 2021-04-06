package ru.roborox.reactive.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.test.context.ContextConfiguration
import ru.roborox.reactive.test.persist.MongoCleanup

@MongoCleanup
@SpringBootTest(
    properties = [
        "roborox.cache.use-locks=false"
    ]
)
@ContextConfiguration(classes = [MockContext::class])
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var mongo: ReactiveMongoOperations
}