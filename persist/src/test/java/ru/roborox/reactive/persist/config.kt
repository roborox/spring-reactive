package ru.roborox.reactive.persist

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.roborox.reactive.json.ObjectMapperUtils
import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.yaml.YamlPropertySource

@Configuration
@EnableRoboroxMongo
@EnableAutoConfiguration
@YamlPropertySource("classpath:/persist-test.yml")
class MockContext {
    @Bean
    fun objectMapper() = ObjectMapperUtils.defaultObjectMapper()
}