package ru.roborox.reactive.mail

import reactor.core.publisher.Mono
import ru.roborox.kotlin.blockingToMono
import java.util.concurrent.Callable

class ReactiveMailSender(
    private val mailSender: MailSender
) {
    fun send(msg: MailMessage): Mono<Void> {
        return Callable { mailSender.send(msg) }.blockingToMono().then()
    }
}


