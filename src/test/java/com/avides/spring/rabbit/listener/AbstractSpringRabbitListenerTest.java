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
import org.springframework.amqp.core.MessageProperties;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@ExtendWith(MockitoExtension.class)
public class AbstractSpringRabbitListenerTest
{
    @InjectMocks
    private SpringRabbitListener<Object> rabbitListener = new ImplementedSpringRabbitListener();

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void testHandle()
    {
        Tags tags = Tags.of(Tag.of("listener", "ImplementedSpringRabbitListener"), Tag.of("from", "sender-app"));
        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        rabbitListener.handle("hello", messageProperties);

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleWithAppIdIsNull()
    {
        Tags tags = Tags.of(Tag.of("listener", "ImplementedSpringRabbitListener"), Tag.of("from", "UNKNOWN"));
        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        rabbitListener.handle("hello", new MessageProperties());

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    private static class ImplementedSpringRabbitListener extends AbstractSpringRabbitListener<Object>
    {
        @Override
        protected void handleEvent(Object object)
        {
            // not necessary
        }
    }
}
