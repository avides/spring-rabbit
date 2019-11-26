package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class RabbitTemplatePropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteRabbitTemplateProperties());
    }

    // exchange
    @Test
    public void testBeanValidationOnExchangeWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setExchange(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    public void testBeanValidationOnExchangeWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "exchange");
    }

    // routingKey
    @Test
    public void testBeanValidationOnRoutingKeyWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    @Test
    public void testBeanValidationOnRoutingKeyWithBlank()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    @Test
    public void testBeanValidationOnRoutingKeyWithEmpty()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    // messageConverter
    @Test
    public void testBeanValidationOnMessageConverterWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setMessageConverter(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    public void testBeanValidationOnMessageConverterWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getMessageConverter().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "messageConverter");
    }

    // connectionFactory
    @Test
    public void testBeanValidationOnConnectionFactoryWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setConnectionFactory(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    public void testBeanValidationOnConnectionFactoryWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getConnectionFactory().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "connectionFactory");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnExchange()
    {
        assertNull(new RabbitTemplateProperties().getExchange());
    }

    @Test
    public void testDefaultValueOnRoutingKey()
    {
        assertNull(new RabbitTemplateProperties().getRoutingkey());
    }

    @Test
    public void testDefaultValueOnBeanName()
    {
        assertNull(new RabbitTemplateProperties().getBeanName());
    }

    @Test
    public void testDefaultValueOnMessageConverter()
    {
        assertNull(new RabbitTemplateProperties().getMessageConverter());
    }

    @Test
    public void testDefaultValueOnConnectionFactory()
    {
        assertNull(new RabbitTemplateProperties().getConnectionFactory());
    }
}
