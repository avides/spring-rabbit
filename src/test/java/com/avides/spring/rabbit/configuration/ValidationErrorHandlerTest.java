package com.avides.spring.rabbit.configuration;

import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

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
    @SuppressWarnings("unchecked")
    public void testHandleErrorWithConstraintViolationExceptionAndWithViolations()
    {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
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
