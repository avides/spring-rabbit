package com.avides.spring.rabbit.configuration.creator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.amqp.core.Exchange;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.configuration.domain.ExchangeProperties.ExchangeType;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class ExchangeCreatorTest implements DomainTestSupport
{
    private Creator<Exchange> creator;

    @Test
    public void testCreateInstanceWithTypeDirect()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setType(ExchangeType.DIRECT);
        creator = new ExchangeCreator(exchangeProperties);

        Exchange exchange = creator.createInstance();

        assertEquals("SpringExchange", exchange.getName());
        assertEquals("direct", exchange.getType());
    }

    @Test
    public void testCreateInstanceWithTypeTopic()
    {
        ExchangeProperties exchangeProperties = getCompleteExchangeProperties();
        exchangeProperties.setType(ExchangeType.TOPIC);
        creator = new ExchangeCreator(exchangeProperties);

        Exchange exchange = creator.createInstance();

        assertEquals("SpringExchange", exchange.getName());
        assertEquals("topic", exchange.getType());
    }
}
