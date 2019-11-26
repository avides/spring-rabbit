package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class RabbitAdminPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteRabbitAdminProperties());
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "beanName");
    }

    // connectionFactory
    @Test
    public void testBeanValidationOnConnectionFactoryWithNull()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.setConnectionFactory(null);
        BeanValidationTestSupport.expectNoError(rabbitAdminProperties);
    }

    @Test
    public void testBeanValidationOnConnectionFactoryWithInvalid()
    {
        RabbitAdminProperties rabbitAdminProperties = getCompleteRabbitAdminProperties();
        rabbitAdminProperties.getConnectionFactory().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitAdminProperties, "connectionFactory");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnBeanName()
    {
        assertEquals("rabbitAdmin", new RabbitAdminProperties().getBeanName());
    }

    @Test
    public void testDefaultValueOnConnectionFactory()
    {
        assertNull(new RabbitAdminProperties().getConnectionFactory());
    }
}
