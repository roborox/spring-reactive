package ru.roborox.reactive.task

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.persist.configuration.IncludePersistProperties

@Configuration
@EnableAutoConfiguration
@EnableRoboroxTask
@EnableRoboroxMongo
@IncludePersistProperties
class MockContext