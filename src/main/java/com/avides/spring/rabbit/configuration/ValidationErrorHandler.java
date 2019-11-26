package com.avides.spring.rabbit.configuration;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationErrorHandler implements ErrorHandler
{
    @Override
    public void handleError(final Throwable throwable)
    {
        if (throwable instanceof ConstraintViolationException)
        {
            handleConstraintViolationException((ConstraintViolationException) throwable);
            return;
        }

        if (throwable.getCause() instanceof ConstraintViolationException)
        {
            handleConstraintViolationException((ConstraintViolationException) throwable.getCause());
            return;
        }

        log.error(throwable.getMessage(), throwable);
    }

    private static void handleConstraintViolationException(final ConstraintViolationException e)
    {
        if (CollectionUtils.isEmpty(e.getConstraintViolations()))
        {
            log.error(e.getMessage(), e);
        }
        else
        {
            log.error(format(e.getConstraintViolations().iterator().next()), e);
        }
    }

    private static String format(ConstraintViolation<?> violation)
    {
        return "INVALID " + violation.getPropertyPath() + " " + violation.getMessage() + ": " + violation.getRootBean() + " -- " + violation.getLeafBean();
    }
}
