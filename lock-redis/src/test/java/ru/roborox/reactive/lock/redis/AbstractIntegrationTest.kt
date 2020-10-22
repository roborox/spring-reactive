package ru.roborox.reactive.lock.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import ru.roborox.reactive.lock.LockService

@ContextConfiguration(classes = [MockConfiguration::class])
@SpringBootTest
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var lockService: LockService
}