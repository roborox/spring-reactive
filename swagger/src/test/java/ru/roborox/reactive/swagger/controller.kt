package ru.roborox.reactive.swagger

import com.fasterxml.classmate.TypeResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import springfox.documentation.schema.AlternateTypeRules
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@RestController
@SpringBootApplication
class TestController {
    @Bean
    fun docket() = Docket(DocumentationType.OAS_30)

    @GetMapping("/simpleTest")
    fun simpleTest(@RequestParam value: String): Mono<String> {
        return Mono.just("result")
    }

    @GetMapping
    fun someTestWithAlternativeRule(): Blablabla {
        return Blablabla("result")
    }
}

@Component
class DocketTypes(docket: Docket, resolver: TypeResolver) {
    init {
        docket.alternateTypeRules(
            AlternateTypeRules.newRule(resolver.resolve(Blablabla::class.java), resolver.resolve(String::class.java))
        )
    }
}

data class Blablabla(val value: String)