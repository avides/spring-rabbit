package com.avides.spring.rabbit.configuration.creator;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.util.StringUtils;

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
        // RabbitProperties.setAddresses(..) takes a List<String> since Spring Boot 3 (previously a comma-separated String)
        rabbitProperties.setAddresses(splitAddresses(customConnectionFactoryProperties.getAddresses()));
        rabbitProperties.setUsername(customConnectionFactoryProperties.getUsername());
        rabbitProperties.setPassword(customConnectionFactoryProperties.getPassword());
        rabbitProperties.setVirtualHost(customConnectionFactoryProperties.getVirtualHost());
        return rabbitProperties;
    }

    private static List<String> splitAddresses(String addresses)
    {
        return Arrays.asList(StringUtils.commaDelimitedListToStringArray(addresses));
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
