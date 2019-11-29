package com.avides.spring.rabbit.listener.container;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.listener.ContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class DefaultMessageListenerContainerTest
{
    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private RabbitListener<Object> rabbitListener;

    @Mock
    private ContextAwareRabbitListener<Object> contextAwareRabbitListener;

    @Mock
    private MessageConverter messageConverter;

    @Test
    public void testConstructor()
    {
        new DefaultMessageListenerContainer<>(connectionFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNull()
    {
        new DefaultMessageListenerContainer<>(null);
    }

    @Test
    public void testSetListener()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(rabbitListener, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetListenerWithoutListener()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(null, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetListenerWithoutMessageConverter()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(rabbitListener, null);
    }

    @Test
    public void testSetContextAwareListener()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(contextAwareRabbitListener, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetContextAwareListenerWithoutListener()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(null, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetContextAwareListenerWithoutMessageConverter()
    {
        DefaultMessageListenerContainer<Object> listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(contextAwareRabbitListener, null);
    }
}
