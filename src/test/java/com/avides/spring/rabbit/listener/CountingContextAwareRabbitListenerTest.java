package com.avides.spring.rabbit.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
public class CountingContextAwareRabbitListenerTest
{
    @InjectMocks
    private ContextAwareRabbitListener<Object> rabbitListener = new ImplementedCountingListener();

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void testHandle()
    {
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));

        rabbitListener.handle("hello", null);
    }

    @Test
    public void testHandleWithObjectSupplierAndMessageProperties()
    {
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));

        rabbitListener.handle(() ->
        {
            return "hello";
        }, MessagePropertiesBuilder.newInstance().build());
    }

    @Test
    public void testHandleWithObjectSupplierAndMessagePropertiesSupplier()
    {
        when(meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener")).thenReturn(mock(Counter.class));

        rabbitListener.handle(() ->
        {
            return "hello";
        }, () ->
        {
            return MessagePropertiesBuilder.newInstance().build();
        });
    }

    private static class ImplementedCountingListener extends CountingContextAwareRabbitListener<Object>
    {
        @Override
        protected void handleEvent(Object object, MessageProperties messageProperties)
        {
            // not necessary
        }
    }
}
