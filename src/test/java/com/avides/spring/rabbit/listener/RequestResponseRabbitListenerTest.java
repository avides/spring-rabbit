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
public class RequestResponseRabbitListenerTest
{
    private RequestResponseRabbitListener<Object> successRabbitListener;

    private RequestResponseRabbitListener<Object> failureRabbitListener;

    @MockStrict
    private RabbitTemplate responseRabbitTemplate;

    @MockStrict
    private MeterRegistry meterRegistry;

    @Before
    public void setup()
    {
        successRabbitListener = new SuccessRequestResponseRabbitListener();
        Whitebox.setInternalState(successRabbitListener, meterRegistry);

        failureRabbitListener = new FailureRequestResponseRabbitListener();
        Whitebox.setInternalState(failureRabbitListener, meterRegistry);
    }

    @Test
    public void testHandleEventWithoutCorrelationId()
    {
        try
        {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setReplyTo("response-queue");
            successRabbitListener.handleEvent("", messageProperties);
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("correlation_id must not be null", e.getMessage());
        }
    }

    @Test
    public void testHandleEventWithoutReplyTo()
    {
        try
        {
            MessageProperties messageProperties = new MessageProperties();
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
        Tags tags = Tags.of(Tag.of("listener", "FailureRequestResponseRabbitListener"), Tag.of("from", "sender-app"));

        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutResponseAndAppId()
    {
        Tags tags = Tags.of(Tag.of("listener", "FailureRequestResponseRabbitListener"));

        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEvent()
    {
        Tags tags = Tags.of(Tag.of("listener", "SuccessRequestResponseRabbitListener"), Tag.of("from", "sender-app"));

        responseRabbitTemplate.convertAndSend(eq(""), eq("response-queue"), eq("response"), EasyMock.anyObject(MessagePostProcessor.class));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutAppId()
    {
        Tags tags = Tags.of(Tag.of("listener", "SuccessRequestResponseRabbitListener"));

        responseRabbitTemplate.convertAndSend(eq(""), eq("response-queue"), eq("response"), EasyMock.anyObject(MessagePostProcessor.class));
        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    private class SuccessRequestResponseRabbitListener extends RequestResponseRabbitListener<Object>
    {
        public SuccessRequestResponseRabbitListener()
        {
            super(responseRabbitTemplate);
        }

        @Override
        protected Object processRequest(Object requestObject)
        {
            return "response";
        }
    }

    private class FailureRequestResponseRabbitListener extends RequestResponseRabbitListener<Object>
    {
        public FailureRequestResponseRabbitListener()
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
