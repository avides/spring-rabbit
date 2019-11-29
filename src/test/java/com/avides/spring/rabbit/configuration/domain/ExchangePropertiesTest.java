package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties.ExchangeType;
import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class ExchangePropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteExchangeProperties());
    }

    // name
    @Test
    public void testBeanValidationOnNameWithNull()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    @Test
    public void testBeanValidationOnNameWithEmpty()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    @Test
    public void testBeanValidationOnNameWithBlank()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "name");
    }

    // type
    @Test
    public void testBeanValidationOnTypeWithNull()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setType(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(exchangeProperties, "type");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnName()
    {
        assertNull(new ExchangeProperties().getName());
    }

    @Test
    public void testDefaultValueOnType()
    {
        assertEquals(ExchangeType.TOPIC, new ExchangeProperties().getType());
    }
}
