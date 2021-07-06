package ru.roborox.reactive.feign

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactivefeign.ReactiveContract
import reactivefeign.ReactiveOptions
import reactivefeign.webclient.WebReactiveFeign
import reactivefeign.webclient.WebReactiveOptions
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

object FeignHelper {
    fun <T> createClient(clazz: Class<T>, mapper: ObjectMapper, baseUrl: String): T {
        return createClient(clazz, mapper, baseUrl, NoopWebClientCustomizer)
    }

    fun <T> createClient(clazz: Class<T>, mapper: ObjectMapper, baseUrl: String, clientCustomizer: WebClientCustomizer = NoopWebClientCustomizer): T {
        val strategies = ExchangeStrategies
            .builder()
            .codecs { clientDefaultCodecsConfigurer: ClientCodecConfigurer ->
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(
                    Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON)
                )
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(
                    Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON)
                )
            }.build()
        val provider = ConnectionProvider.builder("raribleFeign")
            .maxConnections(500)
            .pendingAcquireMaxCount(-1)
            .maxIdleTime(Duration.ofSeconds(60))
            .maxLifeTime(Duration.ofSeconds(60))
            .lifo()
            .build()
        val client = HttpClient.create(provider).tcpConfiguration {
            it.option(ChannelOption.SO_KEEPALIVE, true)
        }.responseTimeout(Duration.ofSeconds(60))
        val connector = ReactorClientHttpConnector(client)
        val builder = WebClient.builder().clientConnector(connector).exchangeStrategies(strategies)
        clientCustomizer.customize(builder)
        return WebReactiveFeign
            .builder<T>(builder)
            .options(WebReactiveOptions.Builder()
                .setReadTimeoutMillis(60000)
                .setWriteTimeoutMillis(60000)
                .setConnectTimeoutMillis(60000)
                .build())
            .contract(ReactiveContract(SpringMvcContract()))
            .target(clazz, baseUrl)
    }

    inline fun <reified T> createClient(mapper: ObjectMapper, baseUrl: String): T {
        return createClient(T::class.java, mapper, baseUrl, NoopWebClientCustomizer)
    }

    inline fun <reified T> createClient(mapper: ObjectMapper, baseUrl: String, clientCustomizer: WebClientCustomizer): T {
        return createClient(T::class.java, mapper, baseUrl, clientCustomizer)
    }
}

object NoopWebClientCustomizer: WebClientCustomizer {
    override fun customize(webClientBuilder: WebClient.Builder?) {
    }

}