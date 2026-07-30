package com.avides.spring.rabbit.utils;

import java.util.Collections;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;

class BeanValidationTestSupportTest
{
    @Test
    void testExpectNoErrorWithoutError()
    {
        Validatable validatable = new Validatable("value1", "value2");
        BeanValidationTestSupport.expectNoError(validatable);
    }

    @Test
    void testExpectNoErrorWithError()
    {
        Validatable validatable = new Validatable("value1", null);

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectNoError(validatable));

        Assertions.assertEquals("Unexpected error occurred (Properties: value2)", e.getMessage());
    }

    @Test
    void testExpectNoErrorOnPropertyWithoutAnyError()
    {
        Validatable validatable = new Validatable("value1", "value2");
        BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1");
    }

    @Test
    void testExpectNoErrorOnPropertyWithErrorOnOtherProperty()
    {
        Validatable validatable = new Validatable("value1", null);
        BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1");
    }

    @Test
    void testExpectNoErrorOnPropertyWithErrorOnExpectedProperty()
    {
        Validatable validatable = new Validatable(null, "value2");

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectNoErrorOnProperty(validatable, "value1"));

        Assertions.assertEquals("Unexpected errors occurred (Properties: value1)", e.getMessage());
    }

    @Test
    void testExpectErrorOnlyOnPropertyWithoutError()
    {
        Validatable validatable = new Validatable("value1", "value2");

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1"));

        Assertions.assertEquals("No error occurred", e.getMessage());
    }

    @Test
    void testExpectErrorOnlyOnPropertyWithError()
    {
        Validatable validatable = new Validatable(null, "value2");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1");
    }

    @Test
    void testExpectErrorOnlyOnPropertyWithErrorOnWrongProperty()
    {
        Validatable validatable = new Validatable("value1", null);

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1"));

        Assertions.assertEquals("Unexpected errors occurred (Properties: value2)", e.getMessage());
    }

    @Test
    void testExpectErrorOnlyOnPropertyWithMultipleErrors()
    {
        Validatable validatable = new Validatable(null, null);

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable, "value1"));

        Assertions.assertEquals("More than one error occurred (Properties: value1, value2)", e.getMessage());
    }

    @Test
    void testExpectErrorOnlyOnPropertyWithOneErrorWithMultipleErroneousChildren()
    {
        Validatable2 validatable2 = new Validatable2("ruleId", new SubValidated(null, null));
        BeanValidationTestSupport.expectErrorOnlyOnProperty(validatable2, "rule");
    }

    @Test
    void testExpectErrorOnPropertyWithoutError()
    {
        Validatable validatable = new Validatable("value1", "value2");

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1"));

        Assertions.assertEquals("No error occurred", e.getMessage());
    }

    @Test
    void testExpectErrorOnPropertyWithError()
    {
        Validatable validatable = new Validatable(null, "value2");
        BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
    }

    @Test
    void testExpectErrorOnPropertyWithErrorOnWrongProperty()
    {
        Validatable validatable = new Validatable("value1", null);

        AssertionError e = Assertions.assertThrows(AssertionError.class, () -> BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1"));

        Assertions.assertEquals("Unexpected errors occurred (Properties: value2)", e.getMessage());
    }

    @Test
    void testExpectErrorOnPropertyWithMultipleErrors()
    {
        Validatable validatable = new Validatable(null, null);
        BeanValidationTestSupport.expectErrorOnProperty(validatable, "value1");
    }

    @Test
    void testValidationPrecisionOnEqualPrefixedPropertiesWithDirectMatch()
    {
        Validatable2 validatable2 = new Validatable2(null, new SubValidated("12345", "name"));
        BeanValidationTestSupport.expectErrorOnProperty(validatable2, "ruleId");
    }

    @Test
    void testValidationPrecisionOnEqualPrefixedPropertiesWithChildProperties()
    {
        Validatable2 validatable2 = new Validatable2(null, new SubValidated("12345", null));
        BeanValidationTestSupport.expectErrorOnProperty(validatable2, "rule");
    }

    @Test
    void testValidationPrecisionOnEqualPrefixedPropertiesWithArrayProperties()
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
