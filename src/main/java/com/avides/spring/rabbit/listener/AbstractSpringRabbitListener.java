package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Abstract implementation of {@link SpringRabbitListener} with metrics.
 * <p>
 * Implementing classes shall either override {@link #handleEvent(Object)} or {@link #handleEvent(Object, MessageProperties)}.
 */
public abstract class AbstractSpringRabbitListener<T> implements SpringRabbitListener<T>
{
    @Lazy
    @Autowired
    protected MeterRegistry meterRegistry;

    /**
     * Handles given unmarshaled message. Called by {@link #handleEvent(Object, MessageProperties)}
     *
     * @param object the unmarshaled object
     */
    protected void handleEvent(T object)
    {
        throw new UnsupportedOperationException("Implementation of AbstractSpringRabbitListener has to override one of the handleEvent methods");
    }

    /**
     * Handles given unmarshaled message with its properties. Called by {@link #handle(Object, MessageProperties)} which also collects some metrics. Calls
     * {@link #handleEvent(Object)} by default which may be overridden.
     *
     * @param object the unmarshaled object
     * @param messageProperties the message properties
     */
    protected void handleEvent(T object, MessageProperties messageProperties)
    {
        handleEvent(object);
    }

    /**
     * Handles given unmarshaled message with its properties and collects some metrics. Calls {@link #handleEvent(Object, MessageProperties)} by default which
     * may be overridden.
     * <p>
     * Metrics are:
     * <ul>
     * <li>rabbit.listener.event
     * <li>rabbit.listener.event.total.duration.milliseconds
     * </ul>
     *
     * @param object the incoming object
     * @param messageProperties the message properties of the message
     */
    @Override
    public void handle(T object, MessageProperties messageProperties)
    {
        long started = System.currentTimeMillis();
        handleEvent(object, messageProperties);
        count(started);
    }

    private void count(long started)
    {
        // avoid the annoying mock of the meterRegistry for unit tests
        if (meterRegistry != null)
        {
            meterRegistry.counter("rabbit.listener.event", "listener", getClass().getSimpleName()).increment();
            long duration = System.currentTimeMillis() - started;
            meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", getClass().getSimpleName()).increment(duration);
        }
    }
}
