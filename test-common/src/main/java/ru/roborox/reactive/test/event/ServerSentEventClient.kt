package ru.roborox.reactive.test.event

import com.google.inject.internal.MoreTypes
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.Disposable
import java.util.*

class ServerSentEventClient<T>(url: String, clazz: Class<T>, vararg vars: Any) : Disposable {
    val collected: MutableList<ServerSentEvent<T>> = Collections.synchronizedList<ServerSentEvent<T>>(ArrayList<ServerSentEvent<T>>())

    private val disposable = {
        val type = ParameterizedTypeReference.forType<ServerSentEvent<T>>(MoreTypes.ParameterizedTypeImpl(null, ServerSentEvent::class.java, clazz))
        WebClient.create()
            .get()
            .uri(url, *vars)
            .retrieve()
            .bodyToFlux(type)
            .subscribe { collected.add(it) }
    }()

    override fun dispose() {
        disposable.dispose()
    }

    companion object {
        inline fun <reified T> create(url: String, vararg vars: Any) : ServerSentEventClient<T> =
            ServerSentEventClient(url, T::class.java, *vars)
    }
}