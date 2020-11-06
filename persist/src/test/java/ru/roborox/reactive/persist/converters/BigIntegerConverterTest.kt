package ru.roborox.reactive.persist.converters

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import ru.roborox.reactive.persist.AbstractIntegrationTest
import java.math.BigInteger

class BigIntegerConverterTest : AbstractIntegrationTest() {
    @Test
    fun saveLoad() {
        val saved = mongo.save(WithBigInteger(ObjectId.get(), BigInteger.ONE)).block()!!
        val found = mongo.findById<WithBigInteger>(saved.id).block()
        assertEquals(saved, found)
    }

    @Test
    fun string() {
        val saved = mongo.save(WithBigInteger(ObjectId.get(), BigInteger.ONE)).block()!!
        mongo.update(WithBigInteger::class.java).matching(Query()).apply(Update.update("value", 1L)).all().block()
        val found = mongo.findById<WithBigInteger>(saved.id).block()
        assertEquals(found, saved)
    }

}

data class WithBigInteger(
    @Id
    val id: ObjectId,
    val value: BigInteger
)