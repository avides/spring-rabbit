package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class RabbitTemplatePropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteRabbitTemplateProperties());
    }

    // exchange
    @Test
    void testBeanValidationOnExchangeWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setExchange(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    void testBeanValidationOnExchangeWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "exchange");
    }

    // routingKey
    @Test
    void testBeanValidationOnRoutingKeyWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    @Test
    void testBeanValidationOnRoutingKeyWithBlank()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    @Test
    void testBeanValidationOnRoutingKeyWithEmpty()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setRoutingkey("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "routingkey");
    }

    // beanName
    @Test
    void testBeanValidationOnBeanNameWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithBlank()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithEmpty()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "beanName");
    }

    // messageConverter
    @Test
    void testBeanValidationOnMessageConverterWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setMessageConverter(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    void testBeanValidationOnMessageConverterWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getMessageConverter().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "messageConverter");
    }

    // connectionFactory
    @Test
    void testBeanValidationOnConnectionFactoryWithNull()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.setConnectionFactory(null);
        BeanValidationTestSupport.expectNoError(rabbitTemplateProperties);
    }

    @Test
    void testBeanValidationOnConnectionFactoryWithInvalid()
    {
        RabbitTemplateProperties rabbitTemplateProperties = getCompleteRabbitTemplateProperties();
        rabbitTemplateProperties.getConnectionFactory().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(rabbitTemplateProperties, "connectionFactory");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnExchange()
    {
        assertNull(new RabbitTemplateProperties().getExchange());
    }

    @Test
    void testDefaultValueOnRoutingKey()
    {
        assertNull(new RabbitTemplateProperties().getRoutingkey());
    }

    @Test
    void testDefaultValueOnBeanName()
    {
        assertNull(new RabbitTemplateProperties().getBeanName());
    }

    @Test
    void testDefaultValueOnMessageConverter()
    {
        assertNull(new RabbitTemplateProperties().getMessageConverter());
    }

    @Test
    void testDefaultValueOnConnectionFactory()
    {
        assertNull(new RabbitTemplateProperties().getConnectionFactory());
    }
}
