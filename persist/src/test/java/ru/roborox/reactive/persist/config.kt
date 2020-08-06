package ru.roborox.reactive.persist

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.persist.configuration.IncludePersistProperties

@Configuration
@EnableRoboroxMongo
@EnableAutoConfiguration
@IncludePersistProperties
class MockContext