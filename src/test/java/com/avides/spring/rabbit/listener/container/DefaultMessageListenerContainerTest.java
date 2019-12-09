package com.avides.spring.rabbit.listener.container;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.listener.ContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;
import com.avides.spring.rabbit.listener.SpringRabbitListener;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class DefaultMessageListenerContainerTest
{
    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    @Deprecated(forRemoval = true)
    private RabbitListener<Object> rabbitListener;

    @Mock
    @Deprecated(forRemoval = true)
    private ContextAwareRabbitListener<Object> contextAwareRabbitListener;

    @Mock
    private SpringRabbitListener<Object> springRabbitListener;

    @Mock
    private MessageConverter messageConverter;

    @Test
    public void testConstructor()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThat(listenerContainer.getConnectionFactory()).isSameAs(connectionFactory);
    }

    @Test
    public void testConstructorWithNull()
    {
        assertThatThrownBy(() -> new DefaultMessageListenerContainer<>(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("connectionFactory must not be null");
    }

    @Test
    public void testSetSpringRabbitListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setSpringRabbitListener(springRabbitListener, messageConverter);
        assertThat(listenerContainer.getMessageListener()).isNotNull();
    }

    @Test
    public void testSetSpringRabbitListenerWithoutListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setSpringRabbitListener(null, messageConverter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("springRabbitListener must not be null");
    }

    @Test
    public void testSetSpringRabbitListenerWithoutMessageConverter()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setSpringRabbitListener(springRabbitListener, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("messageConverter must not be null");
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(rabbitListener, messageConverter);
        assertThat(listenerContainer.getMessageListener()).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    @Deprecated(forRemoval = true)
    public void testSetListenerWithoutListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(null, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    @Deprecated(forRemoval = true)
    public void testSetListenerWithoutMessageConverter()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setListener(rabbitListener, null);
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(contextAwareRabbitListener, messageConverter);
        assertThat(listenerContainer.getMessageListener()).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListenerWithoutListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(null, messageConverter);
    }

    @Test(expected = IllegalArgumentException.class)
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListenerWithoutMessageConverter()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(contextAwareRabbitListener, null);
    }
}
