package com.avides.spring.rabbit.configuration.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.provider.ConnectionFactoryProvider;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class CustomConnectionFactoryCreatorTest implements DomainTestSupport
{
    private Creator<ConnectionFactory> creator = new CustomConnectionFactoryCreator(getCompleteCustomConnectionFactoryProperties());

    @Test
    public void testCreateInstance()
    {
        try (var connectionFactoryProvider = mockStatic(ConnectionFactoryProvider.class))
        {
            connectionFactoryProvider
                    .when(() -> ConnectionFactoryProvider.createConnectionFactory(any(CachingConnectionFactory.class), any(RabbitProperties.class), eq(17562)))
                    .thenAnswer(invocation ->
                    {
                        CachingConnectionFactory cachingConnectionFactory = invocation.getArgument(0);
                        RabbitProperties rabbitProperties = invocation.getArgument(1);

                        assertEquals(List.of("localhost"), rabbitProperties.getAddresses());
                        assertEquals("guest", rabbitProperties.getUsername());
                        assertEquals("guest", rabbitProperties.getPassword());
                        assertEquals("/IT", rabbitProperties.getVirtualHost());

                        assertEquals("localhost", cachingConnectionFactory.getHost());
                        assertEquals(5672, cachingConnectionFactory.getPort());
                        assertEquals("guest", cachingConnectionFactory.getUsername());
                        assertEquals("/IT", cachingConnectionFactory.getVirtualHost());

                        return cachingConnectionFactory;
                    });

            creator.createInstance();

            connectionFactoryProvider
                    .verify(() -> ConnectionFactoryProvider.createConnectionFactory(any(CachingConnectionFactory.class), any(RabbitProperties.class), eq(17562)));
        }
    }
}
