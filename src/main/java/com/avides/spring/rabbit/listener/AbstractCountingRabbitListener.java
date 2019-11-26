package com.avides.spring.rabbit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import io.micrometer.core.instrument.MeterRegistry;

public abstract class AbstractCountingRabbitListener
{
    @Lazy
    @Autowired
    protected MeterRegistry meterRegistry;

    protected void count(long started)
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
