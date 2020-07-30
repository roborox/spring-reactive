package ru.roborox.reactive.logger

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ru.roborox.logging.utils.loggerContext

class LoggerContextFilter(private val headers: LoggerHeaders) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange)
            .loggerContext(this.headers.getLoggerContext(exchange.request))
    }
}