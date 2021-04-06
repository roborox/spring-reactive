package ru.roborox.reactive.cache

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import ru.roborox.reactive.lock.LockService
import ru.roborox.reactive.persist.configuration.IncludePersistProperties

@Configuration
@EnableAutoConfiguration
@EnableRoboroxCache
@IncludePersistProperties
class MockContext {
    @Bean
    fun lockService(): LockService {
        return object : LockService {
            override fun <T> synchronize(name: String, expiresMs: Int, op: Mono<T>): Mono<T> {
                return op
            }
        }
    }
}