package com.avides.spring.rabbit.configuration.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.listener.SpringRabbitListener;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@ExtendWith(MockitoExtension.class)
public class ListenerCreatorTest implements DomainTestSupport
{
    private Creator<DefaultMessageListenerContainer<Object>> creator;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private SpringRabbitListener<Object> contextAwareRabbitListener;

    @Mock
    private SpringRabbitListener<Object> rabbitListener;

    @Test
    public void testCreateInstanceWithContextAwareRabbitListener()
    {
        creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, contextAwareRabbitListener);
        var container = creator.createInstance();

        assertEquals(Arrays.asList("testQueueName"), Arrays.asList(container.getQueueNames()));
        assertEquals(50, getPrefetchCount(container));
        assertEquals(2, getMaxConcurrentConsumers(container));
    }

    @Test
    public void testCreateInstanceWithRabbitListener()
    {
        creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, rabbitListener);
        var container = creator.createInstance();

        assertEquals(Arrays.asList("testQueueName"), Arrays.asList(container.getQueueNames()));
        assertEquals(50, getPrefetchCount(container));
        assertEquals(2, getMaxConcurrentConsumers(container));
    }

    @Test
    public void testCreateInstanceWithFaliedListenerConfiguration()
    {
        try
        {
            creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, Integer.valueOf(12));
            creator.createInstance();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Listener configuration failed (found listener class:class java.lang.Integer)", e.getMessage());
        }
    }
}
