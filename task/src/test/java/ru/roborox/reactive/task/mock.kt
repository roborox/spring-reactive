package ru.roborox.reactive.task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import reactor.core.publisher.FluxSink
import reactor.core.publisher.ReplayProcessor

class MockHandler(
    override val type: String
) : TaskHandler<Int> {
    private val messages = ReplayProcessor.create<Int>()
    val sink: FluxSink<Int> = messages.sink()

    override fun runLongTask(resume: Int?, param: String): Flow<Int> =
        messages.asFlow()
}