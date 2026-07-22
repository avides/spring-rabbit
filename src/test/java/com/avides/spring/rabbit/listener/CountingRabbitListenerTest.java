package com.avides.spring.rabbit.listener;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
public class CountingRabbitListenerTest
{
    @InjectMocks
    private RabbitListener<Object> rabbitListener = new ImplementedCountingListener();

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void testHandle()
    {
        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(durationCounter);

        rabbitListener.handle("hello");

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleWithSupplier()
    {
        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(durationCounter);

        rabbitListener.handle(() ->
        {
            return "hello";
        });

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    private static class ImplementedCountingListener extends CountingRabbitListener<Object>
    {
        @Override
        protected void handleEvent(Object object)
        {
            // not necessary
        }
    }
}
