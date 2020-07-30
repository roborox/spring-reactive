package ru.roborox.reactive.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

public class WebClientHelper {
    public static ClientHttpConnector createConnector(int connectTimeoutMs, int readTimeoutMs) {
        return createConnector(connectTimeoutMs, readTimeoutMs, false);
    }

    public static ClientHttpConnector createConnector(int connectTimeoutMs, int readTimeoutMs, boolean followRedirect) {
        final TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
            .doOnConnected(conn -> {
                conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS));
            });
        return new ReactorClientHttpConnector(HttpClient.from(tcpClient).followRedirect(followRedirect));
    }

    public static ExchangeStrategies createExchangeStrategies(ObjectMapper objectMapper) {
        return createExchangeStrategies(objectMapper, 1048576);
    }

    public static ExchangeStrategies createExchangeStrategies(ObjectMapper objectMapper, int maxInMemorySyzeBytes) {
        return ExchangeStrategies.builder()
            .codecs(conf -> {
                conf.defaultCodecs().maxInMemorySize(maxInMemorySyzeBytes);
                conf.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                conf.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
            })
            .build();
    }
}
