package com.avides.spring.rabbit.listener;

import static org.mockito.Mockito.mock;
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
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));

        rabbitListener.handle("hello");
    }

    @Test
    public void testHandleWithSupplier()
    {
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));

        rabbitListener.handle(() ->
        {
            return "hello";
        });
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
