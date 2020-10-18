package ru.roborox.reactive.swagger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerDocumentationTest() {
    @LocalServerPort
    private var port: Int = 0

    private val mapper: ObjectMapper = ObjectMapper()

    @Test
    fun testChanged() {
        val json = URL("http://localhost:$port/v3/api-docs").openStream().use {
            it.readText()
        }
        val expected = javaClass.classLoader.getResourceAsStream("api-docs.json")!!.use {
            it.readText()
        }
        assertEquals(preprocess(expected), preprocess(json))
    }

    private fun preprocess(json: String): String {
        val node = mapper.readTree(json) as ObjectNode
        node.put("host", "host")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node)
    }

    private fun InputStream.readText(): String {
        return InputStreamReader(this).use { it.readText() }
    }
}