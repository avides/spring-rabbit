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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RunWith(PowerMockRunner.class)
public class CountingRabbitListenerTest
{
    @TestSubject
    private RabbitListener<Object> rabbitListener = new ImplementedCountingListener();

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
        rabbitListener.handle("hello");
        verifyAll();
    }

    @Test
    public void testHandleWithSupplier()
    {
        meterRegistry.counter("rabbit.listener.event", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", "listener", "ImplementedCountingListener");
        expectLastCall().andReturn(mock(Counter.class));

        replayAll();
        rabbitListener.handle(() ->
        {
            return "hello";
        });
        verifyAll();
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
