package com.avides.spring.rabbit.utils;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = PRIVATE)
public class ValidationUtil
{
    @Getter
    @Setter
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static boolean isValid(Object object)
    {
        return validateAndReturn(object).isEmpty();
    }

    public static void validate(Object object)
    {
        Set<ConstraintViolation<Object>> violations = validateAndReturn(object);

        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
    }

    public static Set<ConstraintViolation<Object>> validateAndReturn(Object object)
    {
        return validator.validate(object);
    }
}
