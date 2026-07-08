package com.avides.spring.rabbit.listener;

import static org.mockito.Mockito.mock;
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
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        rabbitListener.handle("hello", messageProperties);
    }

    @Test
    public void testHandleWithAppIdIsNull()
    {
        Tags tags = Tags.of(Tag.of("listener", "ImplementedSpringRabbitListener"), Tag.of("from", "UNKNOWN"));
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        rabbitListener.handle("hello", new MessageProperties());
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
