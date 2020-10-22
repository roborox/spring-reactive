package ru.roborox.reactive.persist

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.test.context.ContextConfiguration
import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.test.persist.MongoCleanup

@MongoCleanup
@SpringBootTest
@ContextConfiguration(classes = [MockContext::class, ObjectMapper::class])
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var mongo: ReactiveMongoOperations
}