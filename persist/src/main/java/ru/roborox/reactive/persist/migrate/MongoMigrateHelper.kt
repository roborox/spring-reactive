package ru.roborox.reactive.persist.migrate

import com.mongodb.client.model.IndexOptions
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.annotation.PostConstruct

@Suppress("MemberVisibilityCanBePrivate")
abstract class MongoMigrateHelper(
    private val collectionName: String
) {
    @Autowired
    protected lateinit var mongo: ReactiveMongoOperations

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)
    protected val collection: Mono<MongoCollection<Document>>
        get() = mongo.getCollection(collectionName)

    @PostConstruct
    fun init() {
        logger.info("Start creating indices for {}", collectionName)

        val existingIndices = collection.flatMapMany { Flux.from(it.listIndexes()) }
            .map { d: Document -> d.getString("name") }
            .toStream().collect(Collectors.toSet())
        val createdIndices = createIndices(existingIndices)
        logger.info("got needed indices: $createdIndices")
        existingIndices.removeAll(createdIndices)
        existingIndices.remove("_id_")
        logger.info("will drop indices: $existingIndices")
        for (index in existingIndices) {
            logger.info("dropping index {}", index)
            collection.flatMap { Mono.from(it.dropIndex(index)) }.block()
        }
        logger.info("End creating indices. Start updates")
        executeUpdates()
        logger.info("End updates")
    }

    protected open fun executeUpdates() {}

    /**
     * Вызывается во время инициации приложения для создания/удаления индексов
     *
     * @param existing - существующие индексы
     * @return список актуальных индексов
     */
    protected open fun createIndices(existing: Set<String>): List<String> {
        return emptyList()
    }

    protected fun createIndex(key: Bson, options: IndexOptions) {
        logger.info("creating index ${options.name} $key")
        collection.flatMap { Mono.from(it.createIndex(key, options)) }.block()
    }

    protected fun createIndices(existing: Set<String>, needed: List<IndexDescriptor>): List<String> {
        return needed.map {
            if (!existing.contains(it.name)) {
                if (it is NewIndex) {
                    createIndex(it.key, it.options.name(it.name))
                } else if (it is ExistingIndex) {
                    logger.warn("index ${it.name} doesn't exist and descriptor is ExistingIndex")
                }
            }
            it.name
        }
    }
}

sealed class IndexDescriptor {
    abstract val name: String
}

data class ExistingIndex(override val name: String) : IndexDescriptor()
data class NewIndex(override val name: String, val key: Bson, val options: IndexOptions) : IndexDescriptor()