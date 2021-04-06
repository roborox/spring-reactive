package ru.roborox.reactive.cache

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableReactiveMongoRepositories(basePackageClasses = [CacheConfiguration::class])
@EnableMongoAuditing
@ComponentScan(basePackageClasses = [CacheConfiguration::class])
class CacheConfiguration