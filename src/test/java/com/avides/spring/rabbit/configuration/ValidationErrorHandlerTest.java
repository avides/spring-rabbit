package com.avides.spring.rabbit.configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.Test;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class ValidationErrorHandlerTest
{
    private ValidationErrorHandler errorHandler = new ValidationErrorHandler();

    static
    {
        Locale.setDefault(new Locale("en", "US"));
    }

    @Test
    public void testHandleErrorWithRuntimeException()
    {
        errorHandler.handleError(new RuntimeException("unknown error"));
    }

    @Test
    public void testHandleErrorWithConstraintViolationExceptionAndWithoutViolations()
    {
        errorHandler.handleError(new ConstraintViolationException("invalid pattern", Collections.emptySet()));
    }

    @Test
    public void testHandleErrorWithConstraintViolationExceptionAndWithViolations()
    {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(ConstraintViolationImpl.forBeanValidation(null, null, null, null, null, null, null, null, null, null, null, null));
        errorHandler.handleError(new ConstraintViolationException("invalid pattern", violations));
    }

    @Test
    public void testHandleErrorWithConstraintViolationAsCause()
    {
        errorHandler.handleError(new RuntimeException(new ConstraintViolationException("invalid pattern", Collections.emptySet())));
    }

    @RequiredArgsConstructor
    @ToString
    private class TestObject
    {
        @NotNull
        private final String var1;

        @NotNull
        private String var2;
    }
}
