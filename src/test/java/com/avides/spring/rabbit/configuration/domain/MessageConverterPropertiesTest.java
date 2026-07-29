package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class MessageConverterPropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteMessageConverterProperties());
    }

    // beanName
    @Test
    void testBeanValidationOnBeanNameWithNull()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithEmpty()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithBlank()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnBeanName()
    {
        assertNull(new MessageConverterProperties().getBeanName());
    }
}
