package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class RabbitAdminPropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteRabbitAdminProperties());
    }

    // beanName
    @Test
    void testBeanValidationOnBeanNameWithNull()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithBlank()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithEmpty()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    // connectionFactory
    @Test
    void testBeanValidationOnConnectionFactoryWithNull()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setConnectionFactory(null);
        BeanValidationTestSupport.expectNoError(rabbitAdminProperties);
    }

    @Test
    void testBeanValidationOnConnectionFactoryWithInvalid()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.getConnectionFactory().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "connectionFactory");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnBeanName()
    {
        assertEquals("rabbitAdmin", new RabbitAdminProperties().getBeanName());
    }

    @Test
    void testDefaultValueOnConnectionFactory()
    {
        assertNull(new RabbitAdminProperties().getConnectionFactory());
    }
}
