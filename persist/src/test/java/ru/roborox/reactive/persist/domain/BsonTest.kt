package ru.roborox.reactive.persist.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class BsonTest(
    val id: ObjectId = ObjectId.get(),
    val value: String?
)