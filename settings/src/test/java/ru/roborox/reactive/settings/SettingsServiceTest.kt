package ru.roborox.reactive.settings

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import ru.roborox.reactive.settings.service.SettingsService
import ru.roborox.reactive.test.persist.MongoCleanup

@MongoCleanup
@SpringBootTest(classes = [MockContext::class])
class SettingsServiceTest {
    @Autowired
    private lateinit var settingsService: SettingsService
    @Autowired
    private lateinit var mongo: ReactiveMongoOperations

    @Test
    fun insertAndGet() {
        val value = RandomStringUtils.randomAlphanumeric(10)
        settingsService.setSetting("simple", value).block()
        assertEquals(value, settingsService.getSetting("simple").block())
    }

    @Test
    fun updateAndGet() {
        mongo.save(Setting("simple", "value")).block()

        val value = RandomStringUtils.randomAlphanumeric(10)
        settingsService.setSetting("simple", value).block()
        assertEquals(value, settingsService.getSetting("simple").block())
    }

}