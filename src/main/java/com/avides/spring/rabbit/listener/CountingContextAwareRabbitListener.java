package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;

/**
 * @deprecated use {@link AbstractSpringRabbitListener}, will be removed soon
 * @param <T> type of unmarshaled object
 */
@Deprecated(forRemoval = true)
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
