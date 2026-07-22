package com.avides.spring.rabbit.utils;

import java.util.Collections;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;

public class BeanValidationTestSupportTest
{
    @Test
    public void testExpectNoErrorWithoutError()
    {
        Validatable validatable = new Validatable("value1", "value2");
        BeanValidationTestSupport.expectNoError(validatable);
    }

    @Test
    public void testExpectNoErrorWithError()
    {
        try
        {
            Validatable validatable = new Validatable("value1", null);
            BeanValidationTestSupport.expectNoError(validatable);
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("Unexpected error occurred (Properties: value2)", e.getMessage());
        }
    }

    @Test
    public void testExpectNoErrorOnPropertyWithoutAnyError()
    {
        Validatable validatable = new Validatable("value1", "value2");
        BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1");
    }

    @Test
    public void testExpectNoErrorOnPropertyWithErrorOnOtherProperty()
    {
        Validatable validatable = new Validatable("value1", null);
        BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1");
    }

    @Test
    public void testExpectNoErrorOnPropertyWithErrorOnExpectedProperty()
    {
        try
        {
            Validatable validatable = new Validatable(null, "value2");
            BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("Unexpected errors occurred (Properties: value1)", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnlyOnPropertyWithoutError()
    {
        try
        {
            Validatable validatable = new Validatable("value1", "value2");
            BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("No error occurred", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnlyOnPropertyWithError()
    {
        Validatable validatable = new Validatable(null, "value2");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1");
    }

    @Test
    public void testExpectErrorOnlyOnPropertyWithErrorOnWrongProperty()
    {
        try
        {
            Validatable validatable = new Validatable("value1", null);
            BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("Unexpected errors occurred (Properties: value2)", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnlyOnPropertyWithMultipleErrors()
    {
        try
        {
            Validatable validatable = new Validatable(null, null);
            BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("More than one error occurred (Properties: value1, value2)", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnlyOnPropertyWithOneErrorWithMultipleErroneousChildren()
    {
        Validatable2 validatable2 = new Validatable2("ruleId", new SubValidated(null, null));
        BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable2, "rule");
    }

    @Test
    public void testExpectErrorOnPropertyWithoutError()
    {
        try
        {
            Validatable validatable = new Validatable("value1", "value2");
            BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("No error occurred", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnPropertyWithError()
    {
        Validatable validatable = new Validatable(null, "value2");
        BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
    }

    @Test
    public void testExpectErrorOnPropertyWithErrorOnWrongProperty()
    {
        try
        {
            Validatable validatable = new Validatable("value1", null);
            BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
            Assertions.fail("Exception expected");
        }
        catch (Throwable e)
        {
            Assertions.assertEquals("Unexpected errors occurred (Properties: value2)", e.getMessage());
        }
    }

    @Test
    public void testExpectErrorOnPropertyWithMultipleErrors()
    {
        Validatable validatable = new Validatable(null, null);
        BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
    }

    @Test
    public void testValidationPrecisionOnEqualPrefixedPropertiesWithDirectMatch()
    {
        Validatable2 validatable2 = new Validatable2(null, new SubValidated("12345", "name"));
        BeanValidationTestSupport.expectErrorOnProperty(validatable2, "ruleId");
    }

    @Test
    public void testValidationPrecisionOnEqualPrefixedPropertiesWithChildProperties()
    {
        Validatable2 validatable2 = new Validatable2(null, new SubValidated("12345", null));
        BeanValidationTestSupport.expectErrorOnProperty(validatable2, "rule");
    }

    @Test
    public void testValidationPrecisionOnEqualPrefixedPropertiesWithArrayProperties()
    {
        Validatable3 validatable3 = new Validatable3(Collections.singletonList(new SubValidated("12345", null)));
        BeanValidationTestSupport.expectErrorOnProperty(validatable3, "rules");
    }

    @RequiredArgsConstructor
    private static class Validatable
    {
        @NotNull
        private final String value1;

        @NotBlank
        private final String value2;
    }

    @RequiredArgsConstructor
    private static class Validatable2
    {
        @NotBlank
        private final String ruleId;

        @NotNull
        @Valid
        private final SubValidated rule;
    }

    @RequiredArgsConstructor
    private static class Validatable3
    {
        @NotNull
        @Valid
        private final List<SubValidated> rules;
    }

    @RequiredArgsConstructor
    private static class SubValidated
    {
        @NotBlank
        private final String id;

        @NotBlank
        private final String name;
    }

}
