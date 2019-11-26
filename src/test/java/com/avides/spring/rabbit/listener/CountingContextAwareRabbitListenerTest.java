package com.avides.spring.rabbit.listener;

import static org.easymock.EasyMock.mock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RunWith(PowerMockRunner.class)
public class CountingContextAwareRabbitListenerTest
{
    @TestSubject
    private ContextAwareRabbitListener<Object> rabbitListener = new ImplementedCountingListener();

    @MockStrict
    private MeterRegistry meterRegistry;

    @Test
    public void testHandle()
    {
        meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        replayAll();
        rabbitListener.handle("hello", null);
        verifyAll();
    }

    @Test
    public void testHandleWithObjectSupplierAndMessageProperties()
    {
        meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        replayAll();
        rabbitListener.handle(() ->
        {
            return "hello";
        }, MessagePropertiesBuilder.newInstance().build());
        verifyAll();
    }

    @Test
    public void testHandleWithObjectSupplierAndMessagePropertiesSupplier()
    {
        meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        replayAll();
        rabbitListener.handle(() ->
        {
            return "hello";
        }, () ->
        {
            return MessagePropertiesBuilder.newInstance().build();
        });
        verifyAll();
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
