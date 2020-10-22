package ru.roborox.reactive.validator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HexValueValidatorTest {
    private val validator = HexValueValidator()

    @Test
    fun withPrefix() {
        assertTrue(validator.isValid("0x1234567890123456789012345678901234567890", null))
    }

    @Test
    fun upperCase() {
        assertTrue(validator.isValid("a234567890123456789012345678901234567890", null))
    }
}