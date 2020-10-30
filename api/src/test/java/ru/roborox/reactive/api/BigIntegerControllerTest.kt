package ru.roborox.reactive.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
            fun getBigint() = BigInteger.TEN

            @GetMapping("/json")
            fun getJsonBigInt() = JsonWithBigInt(BigInteger.TEN)
        }

    }

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun bigintToString() {
        val result = URL("http://localhost:$port/bigint").openConnection().getInputStream().use {
            InputStreamReader(it).use { isr -> isr.readText() }
        }
        Assertions.assertEquals("\"10\"", result)
    }

    @Test
    fun withJson() {
        val result = URL("http://localhost:$port/json").openConnection().getInputStream().use {
            InputStreamReader(it).use { isr -> isr.readText() }
        }
        Assertions.assertEquals("{\"value\":\"10\"}", result)
    }

    @Test
    fun bigintSerializeByMapper() {
        val value = BigInteger.TEN.pow(18)
        Assertions.assertEquals("\"$value\"", mapper.writeValueAsString(value))
    }

    @Test
    fun bigintDeserialize() {
        val value = BigInteger.TEN.pow(18).multiply(BigInteger.valueOf(RandomUtils.nextInt(100, 1000).toLong()))
        Assertions.assertEquals(mapper.readValue(value.toString(), BigInteger::class.java), value)
        Assertions.assertEquals(mapper.readValue("\"$value\"", BigInteger::class.java), value)
    }
}

data class JsonWithBigInt(val value: BigInteger)