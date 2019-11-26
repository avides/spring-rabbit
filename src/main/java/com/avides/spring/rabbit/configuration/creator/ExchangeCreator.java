package com.avides.spring.rabbit.configuration.creator;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExchangeCreator implements Creator<Exchange>
{
    private final ExchangeProperties exchangeProperties;

    @Override
    public Exchange createInstance()
    {
        switch (exchangeProperties.getType())
        {
            case DIRECT:
                return new DirectExchange(exchangeProperties.getName());
            case TOPIC:
                return new TopicExchange(exchangeProperties.getName());
            default:
                throw new IllegalArgumentException("Unsupported ExchangeType " + exchangeProperties.getType());
        }
    }
}
