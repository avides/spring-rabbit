package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.test.support.AbstractIT;

@ActiveProfiles({ "it", "rabbitTemplateManagementWithOneConnectionFactory" })
public class SpringRabbitAutoConfigurationForRabbitTemplateManagementWithOneConnectionFactoryIT extends AbstractIT
{
    @Autowired
    private RabbitTemplate defaultRabbitTemplate;

    @Autowired
    private RabbitTemplate customRabbitTemplate;

    @Autowired
    private List<ConnectionFactory> connectionFactories;

    @Test
    public void test()
    {
        assertEquals(2, connectionFactories.size());
        connectionFactories.forEach(cf -> assertThat(cf).isInstanceOf(ConnectionFactory.class));

        // firstRabbitTemplate
        assertEquals("com.avides.exchange.default", defaultRabbitTemplate.getExchange());
        assertEquals("defaultRabbitTemplate.outbound", defaultRabbitTemplate.getRoutingKey());
        assertEquals("guest", defaultRabbitTemplate.getConnectionFactory().getUsername());
        assertEquals("/", defaultRabbitTemplate.getConnectionFactory().getVirtualHost());

        // secondRabbitTemplate
        assertEquals("com.avides.exchange.custom", customRabbitTemplate.getExchange());
        assertEquals("customRabbitTemplate.outbound", customRabbitTemplate.getRoutingKey());
        assertEquals("guest", customRabbitTemplate.getConnectionFactory().getUsername());
        assertEquals("/", customRabbitTemplate.getConnectionFactory().getVirtualHost());

        // TODO test exchange type
    }
}
