package ru.roborox.reactive.validator;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HexValueValidator implements ConstraintValidator<HexValue, String> {
    public static final Logger logger = LoggerFactory.getLogger(HexValueValidator.class);

    private int size = 20;

    @Override
    public void initialize(HexValue constraintAnnotation) {
        size = constraintAnnotation.size();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null
            || value.length() == 0
            || isValidNonEmpty(value);
    }

    private boolean isValidNonEmpty(String value) {
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        try {
            return Hex.decodeHex(value).length == size;
        } catch (Exception e) {
            logger.warn("unable to parse hex", e);
            return false;
        }
    }
}
