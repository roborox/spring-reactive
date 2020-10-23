package ru.roborox.reactive.api.errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;
import java.util.Locale;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
	private ErrorsTransformer errorsTransformer;

	@Override
	public void serialize(Errors value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		ErrorResponse errorResponse = errorsTransformer.transform(value, Locale.US);
		jgen.writeObject(errorResponse);
	}
	
	@Autowired
	public void setErrorsTransformer(ErrorsTransformer errorsTransformer) {
		this.errorsTransformer = errorsTransformer;
	}
}
