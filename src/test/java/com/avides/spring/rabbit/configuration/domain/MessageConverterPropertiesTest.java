package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class MessageConverterPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteMessageConverterProperties());
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        MessageConverterProperties messageConverterProperties = getCompleteMessageConverterProperties();
        messageConverterProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(messageConverterProperties, "beanName");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnBeanName()
    {
        assertNull(new MessageConverterProperties().getBeanName());
    }
}
