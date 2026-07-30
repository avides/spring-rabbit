package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties.ExchangeType;
import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class ExchangePropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteExchangeProperties());
    }

    // name
    @Test
    void testBeanValidationOnNameWithNull()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    @Test
    void testBeanValidationOnNameWithEmpty()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    @Test
    void testBeanValidationOnNameWithBlank()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    // type
    @Test
    void testBeanValidationOnTypeWithNull()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setType(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "type");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnName()
    {
        assertNull(new ExchangeProperties().getName());
    }

    @Test
    void testDefaultValueOnType()
    {
        assertEquals(ExchangeType.TOPIC, new ExchangeProperties().getType());
    }
}
