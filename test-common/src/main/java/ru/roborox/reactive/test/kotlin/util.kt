package ru.roborox.reactive.test.kotlin

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

inline fun <reified T> ReactiveMongoOperations.get(id: ObjectId): T? = this.findById(id, T::class.java).block()

inline fun <reified T> ReactiveMongoOperations.get(id: String): T? = this.findById(id, T::class.java).block()

inline fun <reified T> ReactiveMongoOperations.count(criteria: Criteria) = this.count(Query(criteria), T::class.java).block()!!

inline fun <reified T> ReactiveMongoOperations.count() = this.count<T>(Criteria())

inline fun <reified T> ReactiveMongoOperations.findOne(criteria: Criteria): T? = this.findOne(Query(criteria), T::class.java).block()

inline fun <reified T> ReactiveMongoOperations.findOne(): T? = this.findOne(Query(Criteria()), T::class.java).block()