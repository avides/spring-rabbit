package com.avides.spring.rabbit.listener;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class RequestResponseSpringRabbitListenerTest
{
    private RequestResponseSpringRabbitListener<Object> successRabbitListener;

    private RequestResponseSpringRabbitListener<Object> failureRabbitListener;

    @MockStrict
    private RabbitTemplate responseRabbitTemplate;

    @MockStrict
    private MeterRegistry meterRegistry;

    @Before
    public void setup()
    {
        successRabbitListener = new SuccessRequestResponseSpringRabbitListener();
        Whitebox.setInternalState(successRabbitListener, meterRegistry);

        failureRabbitListener = new FailureRequestResponseSpringRabbitListener();
        Whitebox.setInternalState(failureRabbitListener, meterRegistry);
    }

    @Test
    public void testHandleEventWithoutCorrelationId()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        responseRabbitTemplate.convertAndSend(eq(""), eq("response-queue"), eq("response"), EasyMock.anyObject(MessagePostProcessor.class));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutReplyTo()
    {
        try
        {
            var messageProperties = new MessageProperties();
            messageProperties.setCorrelationId("request1");
            successRabbitListener.handleEvent("", messageProperties);
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("reply_to must not be null", e.getMessage());
        }
    }

    @Test
    public void testHandleEventWithoutResponse()
    {
        var tags = Tags.of(Tag.of("listener", "FailureRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutResponseAndAppId()
    {
        var tags = Tags.of(Tag.of("listener", "FailureRequestResponseSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEvent()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        responseRabbitTemplate.convertAndSend(eq(""), eq("response-queue"), eq("response"), EasyMock.anyObject(MessagePostProcessor.class));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutAppId()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        responseRabbitTemplate.convertAndSend(eq(""), eq("response-queue"), eq("response"), EasyMock.anyObject(MessagePostProcessor.class));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    private class SuccessRequestResponseSpringRabbitListener extends RequestResponseSpringRabbitListener<Object>
    {
        public SuccessRequestResponseSpringRabbitListener()
        {
            super(responseRabbitTemplate);
        }

        @Override
        protected Object processRequest(Object requestObject)
        {
            return "response";
        }
    }

    private class FailureRequestResponseSpringRabbitListener extends RequestResponseSpringRabbitListener<Object>
    {
        public FailureRequestResponseSpringRabbitListener()
        {
            super(responseRabbitTemplate);
        }

        @Override
        protected Object processRequest(Object requestObject)
        {
            return null;
        }
    }
}
