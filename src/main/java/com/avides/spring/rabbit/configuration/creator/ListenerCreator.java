package com.avides.spring.rabbit.configuration.creator;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.listener.RabbitListener;
import com.avides.spring.rabbit.listener.SpringRabbitListener;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListenerCreator implements Creator<DefaultMessageListenerContainer<Object>>
{
    private final ConnectionFactory connectionFactory;

    private final String queueName;

    private final int prefetchCount;

    private final int maxConcurrentConsumers;

    private final MessageConverter messageConverter;

    private final Object listener;

    @Override
    public DefaultMessageListenerContainer<Object> createInstance()
    {
        DefaultMessageListenerContainer<Object> container = new DefaultMessageListenerContainer<>(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(prefetchCount);
        container.setMaxConcurrentConsumers(maxConcurrentConsumers);
        apppendListenerToContainer(container, messageConverter);
        return container;
    }

    @SuppressWarnings("unchecked")
    private void apppendListenerToContainer(DefaultMessageListenerContainer<Object> container, MessageConverter converter)
    {
        if (listener instanceof SpringRabbitListener<?>)
        {
            container.setSpringRabbitListener((SpringRabbitListener<Object>) listener, converter);
        }
        else if (listener instanceof RabbitListener<?>)
        {
            container.setListener(((RabbitListener<Object>) listener), converter);
        }
        else
        {
            throw new IllegalArgumentException("Listener configuration failed (found listener class:" + listener.getClass() + ")");
        }
    }
}
