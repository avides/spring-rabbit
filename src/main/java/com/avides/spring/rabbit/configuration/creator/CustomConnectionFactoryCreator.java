package com.avides.spring.rabbit.configuration.creator;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.domain.CustomConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.provider.ConnectionFactoryProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomConnectionFactoryCreator implements Creator<ConnectionFactory>
{
    private final CustomConnectionFactoryProperties customConnectionFactoryProperties;

    @Override
    public ConnectionFactory createInstance()
    {
        RabbitProperties rabbitProperties = createRabbitProperties();
        CachingConnectionFactory factory = createCachingConnectionFactory(rabbitProperties);
        return ConnectionFactoryProvider.createConnectionFactory(factory, rabbitProperties, customConnectionFactoryProperties.getApiPort());
    }

    private RabbitProperties createRabbitProperties()
    {
        RabbitProperties rabbitProperties = new RabbitProperties();
        rabbitProperties.setAddresses(customConnectionFactoryProperties.getAddresses());
        rabbitProperties.setUsername(customConnectionFactoryProperties.getUsername());
        rabbitProperties.setPassword(customConnectionFactoryProperties.getPassword());
        rabbitProperties.setVirtualHost(customConnectionFactoryProperties.getVirtualHost());
        return rabbitProperties;
    }

    private static CachingConnectionFactory createCachingConnectionFactory(RabbitProperties rabbitProperties)
    {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitProperties.determineHost());
        factory.setAddresses(rabbitProperties.determineAddresses());
        factory.setPort(rabbitProperties.determinePort());
        factory.setUsername(rabbitProperties.determineUsername());
        factory.setPassword(rabbitProperties.determinePassword());
        factory.setVirtualHost(rabbitProperties.determineVirtualHost());
        return factory;
    }
}
