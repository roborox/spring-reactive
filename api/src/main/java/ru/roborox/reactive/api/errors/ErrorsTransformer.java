package ru.roborox.reactive.api.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

import static java.util.Arrays.asList;

@Component
public class ErrorsTransformer {
	@Value("${fieldErrorsAsGeneric:false}")
	private boolean fieldErrorsAsGeneric;

	private MessageSource messageSource;
	
	public ErrorResponse transform(Errors errors, Locale locale) {
		final Map<String, List<String>> fieldErrors = new HashMap<>();
		final List<String> genericErrors = new ArrayList<>();
		for (ObjectError error : errors.getAllErrors()) {
			if (error instanceof FieldError) {
				FieldError err = (FieldError) error;
				if (fieldErrorsAsGeneric) {
					genericErrors.add(err.getField() + ": " + getErrorMessage(locale, error));
				} else {
					fieldErrors.computeIfAbsent(err.getField(), field -> new ArrayList<>()).add(getErrorMessage(locale, error));
				}
			} else {
				genericErrors.add(getErrorMessage(locale, error));
			}
		}
		return new ErrorResponse(fieldErrors, genericErrors);
	}

	public ErrorResponse transformFieldError(String fieldName, String message) {
		if (fieldErrorsAsGeneric) {
			return new ErrorResponse(Collections.emptyMap(), asList(fieldName + ": " + message));
		} else {
			return new ErrorResponse(Collections.singletonMap(fieldName, asList(message)), Collections.emptyList());
		}
	}

	private String getErrorMessage(Locale locale, ObjectError error) {
		for (String code : error.getCodes()) {
			String errorMessage = messageSource.getMessage(code, error.getArguments(), "", locale);
			if (StringUtils.hasText(errorMessage)) {
				return errorMessage;
			}
		}
		return messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), locale);
	}
	
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
