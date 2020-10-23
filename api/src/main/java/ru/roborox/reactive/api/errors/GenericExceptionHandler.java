package ru.roborox.reactive.api.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;
import ru.roborox.logging.utils.LoggingUtils;

@ControllerAdvice
@Order
public class GenericExceptionHandler extends AbstractExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<ApiError>> genericException(ServerHttpRequest request, Throwable ex) {
		return LoggingUtils.withMarker(marker -> {
			logger.error(marker, buildMessage(request, ex), ex);
			return Mono.just(new ResponseEntity<>(new ApiError(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR));
		});
	}
}
