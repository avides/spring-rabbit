package com.avides.spring.rabbit.listener.container;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.Assert;

import com.avides.spring.rabbit.configuration.ValidationErrorHandler;
import com.avides.spring.rabbit.converter.SpringRabbitMessageConverter;
import com.avides.spring.rabbit.listener.ContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;

/**
 * Extension of the {@link SimpleMessageListenerContainer} to avoid the Spring implementation of RabbitListeners via annotations
 * <p>
 * Spring uses reflection for every incoming {@link Message}. Therefore, the performance is not as good as possible.
 *
 * @param <T> expected type of the incoming object
 */
public class DefaultMessageListenerContainer<T> extends SimpleMessageListenerContainer
{
    /**
     * Create a listener container from the connection factory.
     *
     * @param connectionFactory the ConnectionFactory to use for obtaining RabbitMQ {@link Connection Connections}.
     */
    public DefaultMessageListenerContainer(ConnectionFactory connectionFactory)
    {
        Assert.notNull(connectionFactory, "connectionFactory must not be null");

        setConnectionFactory(connectionFactory);
        setErrorHandler(new ValidationErrorHandler());
        setDefaultRequeueRejected(false);
        setPrefetchCount(500);
    }

    /**
     * Sets the {@link RabbitListener} and the {@link MessageConverter}
     *
     * @param rabbitListener the {@link RabbitListener} to be added
     * @param messageConverter the {@link MessageConverter} to be added to unmarshal the incoming {@link Message#getBody()}
     */
    public void setListener(RabbitListener<T> rabbitListener, MessageConverter messageConverter)
    {
        setMessageListener(resolveMessageListenerAdapter(rabbitListener, messageConverter));
    }

    /**
     * Sets the {@link ContextAwareRabbitListener} and the {@link MessageConverter}
     *
     * @param rabbitListener the {@link ContextAwareRabbitListener} to be added
     * @param messageConverter the {@link MessageConverter} to be added to unmarshal the incoming {@link Message#getBody()}
     */
    public void setContextAwareListener(ContextAwareRabbitListener<T> rabbitListener, MessageConverter messageConverter)
    {
        setMessageListener(resolveMessageListenerAdapter(rabbitListener, messageConverter));
    }

    private MessageListenerAdapter resolveMessageListenerAdapter(RabbitListener<T> rabbitListener, MessageConverter messageConverter)
    {
        Assert.notNull(rabbitListener, "rabbitListener must not be null");
        Assert.notNull(messageConverter, "messageConverter must not be null");

        if (messageConverter instanceof SpringRabbitMessageConverter)
        {
            Class<T> listenerClassType = rabbitListener.getGenericTypeClass();
            return new MessageListenerAdapter((MessageListener) message ->
            {
                T object = ((SpringRabbitMessageConverter) messageConverter).fromMessage(message, listenerClassType);
                rabbitListener.handle(object);
            });
        }
        return new MessageListenerAdapter((MessageListener) message ->
        {
            @SuppressWarnings("unchecked")
            T object = (T) messageConverter.fromMessage(message);
            rabbitListener.handle(object);
        });
    }

    private MessageListenerAdapter resolveMessageListenerAdapter(ContextAwareRabbitListener<T> rabbitListener, MessageConverter messageConverter)
    {
        Assert.notNull(rabbitListener, "rabbitListener must not be null");
        Assert.notNull(messageConverter, "messageConverter must not be null");

        if (messageConverter instanceof SpringRabbitMessageConverter)
        {
            Class<T> listenerClassType = rabbitListener.getGenericTypeClass();
            return new MessageListenerAdapter((MessageListener) message ->
            {
                T object = ((SpringRabbitMessageConverter) messageConverter).fromMessage(message, listenerClassType);
                rabbitListener.handle(object, message.getMessageProperties());
            });
        }
        return new MessageListenerAdapter((MessageListener) message ->
        {
            @SuppressWarnings("unchecked")
            T object = (T) messageConverter.fromMessage(message);
            rabbitListener.handle(object, message.getMessageProperties());
        });
    }
}
