package ru.roborox.reactive.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.roborox.reactive.exceptions.NotFoundException
import java.net.HttpURLConnection
import java.net.URL

@SpringBootTest(classes = [NotFoundExceptionTest.Companion.Controller::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotFoundExceptionTest {
    companion object {
        @EnableAutoConfiguration
        @Configuration
        @RestController
        @EnableRoboroxApi
        class Controller {
            @GetMapping("/testException")
            fun test() {
                throw NotFoundException()
            }
        }

    }

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun testHttpStatus() {
        val conn = URL("http://localhost:$port/testException").openConnection() as HttpURLConnection
        Assertions.assertEquals(conn.responseCode, 404)
    }
}
