package com.avides.spring.rabbit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.junit.Test;

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

    @Test(expected = ConstraintViolationException.class)
    public void testValidateWithInvalid()
    {
        TestObject testObject = new TestObject("");
        ValidationUtil.validate(testObject);
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
