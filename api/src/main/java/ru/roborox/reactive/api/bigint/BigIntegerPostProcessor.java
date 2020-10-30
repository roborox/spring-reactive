package ru.roborox.reactive.api.bigint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigInteger;

@JsonComponent
public class BigIntegerPostProcessor {

    public static class Serializer extends StdScalarSerializer<BigInteger> {

        public Serializer() {
            super(BigInteger.class);
        }

        @Override
        public void serialize(BigInteger value, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(value.toString());
        }
    }

    public static class Deserializer extends StdScalarDeserializer<BigInteger> {

        public Deserializer() {
            super(BigInteger.class);
        }

        @Override
        public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken token = p.currentToken();
            switch (token) {
                case VALUE_STRING:
                case VALUE_NUMBER_INT:
                    return new BigInteger(p.getText().trim());
                default:
                    return (BigInteger) ctxt.handleUnexpectedToken(_valueClass, p);
            }
        }
    }
}
