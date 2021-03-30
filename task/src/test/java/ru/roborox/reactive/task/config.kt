package ru.roborox.reactive.task

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.persist.configuration.IncludePersistProperties

@Configuration
@EnableAutoConfiguration
@EnableRoboroxTask
@EnableRoboroxMongo
@IncludePersistProperties
class MockContext {
    @Bean
    @Qualifier("mockHandler1")
    fun mockHandler1() = MockHandler("MOCK1")

    @Bean
    @Qualifier("mockHandler2")
    fun mockHandler2() = MockHandler("MOCK2")
}