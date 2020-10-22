package ru.roborox.reactive.validator;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueFieldValidator implements ConstraintValidator<UniqueField, List<?>> {
    public static final Logger logger = LoggerFactory.getLogger(UniqueFieldValidator.class);

    private String field;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Set test = new HashSet();
            for (Object item : value) {
                String fieldValue = BeanUtils.getProperty(item, field);
                if (test.contains(fieldValue)) {
                    return false;
                } else {
                    test.add(fieldValue);
                }
            }
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
