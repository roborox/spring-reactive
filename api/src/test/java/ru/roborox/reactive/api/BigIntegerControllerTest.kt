package ru.roborox.reactive.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.URL

@SpringBootTest(classes = [BigIntegerControllerTest.Companion.Controller::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BigIntegerControllerTest {
    companion object {
        @EnableAutoConfiguration
        @Configuration
        @RestController
        @EnableRoboroxApi
        class Controller {
            @GetMapping("/bigint")
            fun getBigint(): BigInteger = BigInteger.TEN
        }

    }

    @LocalServerPort
    private var port: Int = 0

    @Test
    fun bigintToString() {
        val result = URL("http://localhost:$port/bigint").openConnection().getInputStream().use {
            InputStreamReader(it).use { isr -> isr.readText() }
        }
        Assertions.assertEquals("10", result)
    }
}