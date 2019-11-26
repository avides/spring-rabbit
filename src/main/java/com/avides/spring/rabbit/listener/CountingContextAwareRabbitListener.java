package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;

public abstract class CountingContextAwareRabbitListener<T> extends AbstractCountingRabbitListener implements ContextAwareRabbitListener<T>
{
    @Override
    public void handle(T object, MessageProperties messageProperties)
    {
        long started = System.currentTimeMillis();
        handleEvent(object, messageProperties);
        count(started);
    }

    protected abstract void handleEvent(T object, MessageProperties messageProperties);
}
