package ru.roborox.reactive.persist.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document("double_test")
data class DoubleTest(
    val value: Double,
    val id: ObjectId = ObjectId.get()
);