package com.avides.spring.rabbit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class ValidationUtilTest
{
    @Test
    public void testIsValidWithValid()
    {
        TestObject testObject = new TestObject("test");
        assertTrue(ValidationUtil.isValid(testObject));
    }

    @Test
    public void testIsValidWithInvalid()
    {
        TestObject testObject = new TestObject("");
        assertFalse(ValidationUtil.isValid(testObject));
    }

    @Test
    public void testValidateWithValid()
    {
        TestObject testObject = new TestObject("test");
        ValidationUtil.validate(testObject);
    }

    @Test
    public void testValidateWithInvalid()
    {
        TestObject testObject = new TestObject("");
        assertThrows(ConstraintViolationException.class, () -> ValidationUtil.validate(testObject));
    }

    @Test
    public void testValidateAndReturnWithValid()
    {
        TestObject testObject = new TestObject("test");
        assertTrue(ValidationUtil.validateAndReturn(testObject).isEmpty());
    }

    @Test
    public void testValidateAndReturnWithInvalid()
    {
        TestObject testObject = new TestObject("");
        assertEquals(1, ValidationUtil.validateAndReturn(testObject).size());
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    @ToString
    private static class TestObject
    {
        @NotBlank
        private final String value1;

        @Valid
        private SubTestObject subObject;

        @AllArgsConstructor
        @ToString
        private static class SubTestObject
        {
            @NotBlank
            private String value2;
        }
    }
}
