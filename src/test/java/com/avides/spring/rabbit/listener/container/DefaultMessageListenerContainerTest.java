package com.avides.spring.rabbit.listener.container;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import com.avides.spring.rabbit.configuration.ValidationErrorHandler;
import com.avides.spring.rabbit.listener.ContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;
import com.avides.spring.rabbit.listener.SpringRabbitListener;

@ExtendWith(MockitoExtension.class)
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
        assertThat(ReflectionTestUtils.getField(listenerContainer, "errorHandler")).isInstanceOf(ValidationErrorHandler.class);
        assertThat(ReflectionTestUtils.getField(listenerContainer, "defaultRequeueRejected")).isEqualTo(Boolean.FALSE);
        assertThat(ReflectionTestUtils.getField(listenerContainer, "prefetchCount")).isEqualTo(Integer.valueOf(500));
        assertThat(ReflectionTestUtils.getField(listenerContainer, "missingQueuesFatal")).isEqualTo(Boolean.FALSE);
        // declarationRetries is set to "infinity" via the public SimpleMessageListenerContainer#setDeclarationRetries(int) API
        // (previously required overriding the internal createBlockingQueueConsumer() extension point)
        assertThat(ReflectionTestUtils.getField(listenerContainer, "declarationRetries")).isEqualTo(Integer.valueOf(1000000));
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

    @Test
    @Deprecated(forRemoval = true)
    public void testSetListenerWithoutListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setListener(null, messageConverter)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetListenerWithoutMessageConverter()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setListener(rabbitListener, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        listenerContainer.setContextAwareListener(contextAwareRabbitListener, messageConverter);
        assertThat(listenerContainer.getMessageListener()).isNotNull();
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListenerWithoutListener()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setContextAwareListener(null, messageConverter)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Deprecated(forRemoval = true)
    public void testSetContextAwareListenerWithoutMessageConverter()
    {
        var listenerContainer = new DefaultMessageListenerContainer<>(connectionFactory);
        assertThatThrownBy(() -> listenerContainer.setContextAwareListener(contextAwareRabbitListener, null)).isInstanceOf(IllegalArgumentException.class);
    }
}
