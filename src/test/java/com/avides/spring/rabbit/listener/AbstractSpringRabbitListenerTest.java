package com.avides.spring.rabbit.listener;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.MessageProperties;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@RunWith(PowerMockRunner.class)
public class AbstractSpringRabbitListenerTest
{
    @TestSubject
    private SpringRabbitListener<Object> rabbitListener = new ImplementedSpringRabbitListener();

    @MockStrict
    private MeterRegistry meterRegistry;

    @Test
    public void testHandle()
    {
        Tags tags = Tags.of(Tag.of("listener", "ImplementedSpringRabbitListener"), Tag.of("from", "sender-app"));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        rabbitListener.handle("hello", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleWithAppIdIsNull()
    {
        Tags tags = Tags.of(Tag.of("listener", "ImplementedSpringRabbitListener"), Tag.of("from", ""));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        rabbitListener.handle("hello", new MessageProperties());
        verifyAll();
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
