package com.avides.spring.rabbit.listener;

/**
 * @deprecated use {@link AbstractSpringRabbitListener}, will be removed soon
 * @param <T> type of unmarshaled object
 */
@Deprecated(forRemoval = true)
public abstract class CountingRabbitListener<T> extends AbstractCountingRabbitListener implements RabbitListener<T>
{
    @Override
    public void handle(T object)
    {
        long started = System.currentTimeMillis();
        handleEvent(object);
        count(started);
    }

    protected abstract void handleEvent(T object);
}
