package ru.roborox.reactive.api.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.roborox.logging.utils.LoggingUtils;
import ru.roborox.reactive.exceptions.HttpStatusException;
import ru.roborox.reactive.exceptions.NotFoundException;

@ControllerAdvice
@Order(1)
public class ControllerExceptionHandler extends AbstractExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Errors>> validationError1(ServerHttpRequest request, WebExchangeBindException ex) {
		return LoggingUtils.withMarker(marker -> {
			logger.error(marker, buildMessage(request, ex), ex);
			return Mono.just(new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST));
		});
	}

	@ExceptionHandler(BindException.class)
	public Mono<ResponseEntity<Errors>> validationError2(ServerHttpRequest request, BindException ex) {
		return LoggingUtils.withMarker(marker -> {
			logger.error(marker, buildMessage(request, ex), ex);
			return Mono.just(new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST));
		});
	}

	@ExceptionHandler(HttpStatusException.class)
	public Mono<ResponseEntity<Void>> error(ServerHttpRequest request, HttpStatusException ex) {
		return LoggingUtils.withMarker(marker -> {
			logger.trace(marker, buildMessage(request, ex));
			return Mono.just(new ResponseEntity<>((Void) null, ex.getHttpStatus()));
		});
	}

	@ExceptionHandler(ResponseStatusException.class)
	public Mono<ResponseEntity<Object>> error(ServerHttpRequest request, ResponseStatusException ex) {
		return LoggingUtils.withMarker(marker -> {
			HttpStatus status = ex.getStatus();
			final Object result;
			if (status.is5xxServerError()) {
				result = new ApiError(ex.getMessage());
				logger.error(marker, buildMessage(request, ex), ex);
			} else if (status == HttpStatus.NOT_FOUND) {
				result = null;
				logger.trace(marker, buildMessage(request, ex));
			} else if (status == HttpStatus.BAD_REQUEST) {
				if (!StringUtils.isEmpty(ex.getReason())) {
					result = new ApiError(ex.getReason());
				} else {
					result = null;
				}
				logger.warn(marker, buildMessage(request, ex));
			} else {
				result = new ApiError(ex.getMessage());
				logger.trace(marker, buildMessage(request, ex));
			}
			return Mono.just(new ResponseEntity<>(result, status));
		});
	}
}
