package ru.roborox.reactive.persist.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import ru.roborox.reactive.persist.decimal128.BigIntegerToDecimal128Converter;
import ru.roborox.reactive.persist.decimal128.Decimal128ToBigIntegerConverter;

import java.math.BigInteger;
import java.util.Optional;

public class BigIntegerCustomMongoConverter implements SimpleMongoConverter<Decimal128, BigInteger> {
    @Override
    public boolean isSimpleType(Class<?> aClass) {
        return Decimal128.class == aClass || String.class == aClass;
    }

    @Override
    public Optional<Class<?>> getCustomWriteTarget(Class<?> sourceType) {
        if (sourceType == BigInteger.class) {
            return Optional.of(String.class);
        }
        return Optional.empty();
    }

    @Override
    public Converter<Decimal128, BigInteger> getFromMongoConverter() {
        return new Decimal128ToBigIntegerConverter();
    }

    @Override
    public Converter<BigInteger, Decimal128> getToMongoConverter() {
        return new BigIntegerToDecimal128Converter();
    }
}