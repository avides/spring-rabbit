package com.avides.spring.rabbit.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanValidationTestSupport
{
    public void expectNoError(Object object)
    {
        Set<ConstraintViolation<Object>> violations = ValidationUtil.validateAndReturn(object);

        if (!violations.isEmpty())
        {
            throw new AssertionError("Unexpected error occurred (" + getPropertiesLabel(violations) + ")");
        }
    }

    public void expectNoErrorOnProperty(Object object, String property)
    {
        Set<ConstraintViolation<Object>> violations = ValidationUtil.validateAndReturn(object);

        if (hasViolationOnProperty(violations, property))
        {
            throw new AssertionError("Unexpected errors occurred (" + getPropertiesLabel(violations) + ")");
        }
    }

    public void expectErrorOnlyOnProperty(Object object, String property)
    {
        Set<ConstraintViolation<Object>> violations = ValidationUtil.validateAndReturn(object);

        if (violations.stream().map(BeanValidationTestSupport::getPropertyPathRoot).distinct().count() > 1)
        {
            throw new AssertionError("More than one error occurred (" + getPropertiesLabel(violations) + ")");
        }

        if (violations.isEmpty())
        {
            throw new AssertionError("No error occurred");
        }

        if (!hasViolationOnProperty(violations, property))
        {
            throw new AssertionError("Unexpected errors occurred (" + getPropertiesLabel(violations) + ")");
        }
    }

    private static String getPropertyPathRoot(ConstraintViolation<?> violation)
    {
        return violation.getPropertyPath().iterator().next().getName();
    }

    public void expectErrorOnProperty(Object object, String property)
    {
        Set<ConstraintViolation<Object>> violations = ValidationUtil.validateAndReturn(object);

        if (violations.isEmpty())
        {
            throw new AssertionError("No error occurred");
        }

        if (!hasViolationOnProperty(violations, property))
        {
            throw new AssertionError("Unexpected errors occurred (" + getPropertiesLabel(violations) + ")");
        }
    }

    private static String getPropertiesLabel(Set<ConstraintViolation<Object>> violationsSet)
    {
        List<String> properties = new ArrayList<>();

        for (ConstraintViolation<Object> violation : violationsSet)
        {
            properties.add(violation.getPropertyPath().toString());
        }

        Collections.sort(properties);
        StringBuilder propertiesLabel = new StringBuilder("Properties: ");

        for (String property : properties)
        {
            propertiesLabel.append(property + ", ");
        }

        String conc = propertiesLabel.toString();
        return conc.substring(0, conc.length() - 2);
    }

    private static boolean hasViolationOnProperty(Set<ConstraintViolation<Object>> violations, String property)
    {
        for (ConstraintViolation<Object> violation : violations)
        {
            String propertyPath = violation.getPropertyPath().toString();

            // Direct match (id => id)
            if (property.equals(propertyPath))
            {
                return true;
            }

            // Child properties (rule => rule.id)
            if (propertyPath.contains(property + "."))
            {
                return true;
            }

            // Array properties (rules => rules[x])
            if (propertyPath.contains(property + "["))
            {
                return true;
            }
        }

        return false;
    }
}
