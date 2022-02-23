package com.avides.spring.rabbit.configuration.creator;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.listener.ContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
public class ListenerCreatorTest implements DomainTestSupport
{
    private Creator<DefaultMessageListenerContainer<Object>> creator;

    @MockStrict
    private ConnectionFactory connectionFactory;

    @MockStrict
    private MessageConverter messageConverter;

    @MockStrict
    private ContextAwareRabbitListener<Object> contextAwareRabbitListener;

    @MockStrict
    private RabbitListener<Object> rabbitListener;

    @Test
    public void testCreateInstanceWithContextAwareRabbitListener() throws Exception
    {
        replayAll();
        creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, contextAwareRabbitListener);
        var container = creator.createInstance();
        verifyAll();

        assertEquals(Arrays.asList("testQueueName"), Arrays.asList(container.getQueueNames()));
        assertEquals(50, getPrefetchCount(container));
        assertEquals(2, getMaxConcurrentConsumers(container));
    }

    @Test
    public void testCreateInstanceWithRabbitListener()
    {
        replayAll();
        creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, rabbitListener);
        var container = creator.createInstance();
        verifyAll();

        assertEquals(Arrays.asList("testQueueName"), Arrays.asList(container.getQueueNames()));
        assertEquals(50, getPrefetchCount(container));
        assertEquals(2, getMaxConcurrentConsumers(container));
    }

    @Test
    public void testCreateInstanceWithFaliedListenerConfiguration()
    {
        try
        {
            replayAll();
            creator = new ListenerCreator(connectionFactory, "testQueueName", 50, 2, messageConverter, Integer.valueOf(12));
            creator.createInstance();
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Listener configuration failed (found listener class:class java.lang.Integer)", e.getMessage());
        }
    }
}
