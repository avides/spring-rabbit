package com.avides.spring.rabbit.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil
{
    @Getter
    @Setter
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public boolean isValid(Object object)
    {
        return validateAndReturn(object).isEmpty();
    }

    public void validate(Object object)
    {
        Set<ConstraintViolation<Object>> violations = validateAndReturn(object);

        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
    }

    public Set<ConstraintViolation<Object>> validateAndReturn(Object object)
    {
        return validator.validate(object);
    }
}
