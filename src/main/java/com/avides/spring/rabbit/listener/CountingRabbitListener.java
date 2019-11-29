package com.avides.spring.rabbit.listener;

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
