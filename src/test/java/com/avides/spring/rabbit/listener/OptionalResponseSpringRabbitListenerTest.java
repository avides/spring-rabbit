package com.avides.spring.rabbit.listener;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
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
public class OptionalResponseSpringRabbitListenerTest
{
    private OptionalResponseSpringRabbitListener<Object> successRabbitListener;

    private OptionalResponseSpringRabbitListener<Object> failureRabbitListener;

    @MockStrict
    private RabbitTemplate responseRabbitTemplate;

    @MockStrict
    private MeterRegistry meterRegistry;

    @Before
    public void setup()
    {
        successRabbitListener = new SuccessOptionalSpringRabbitListener();
        Whitebox.setInternalState(successRabbitListener, meterRegistry);

        failureRabbitListener = new FailureOptionalSpringRabbitListener();
        Whitebox.setInternalState(failureRabbitListener, meterRegistry);
    }

    @Test
    public void testHandleEventWithoutReplyTo()
    {
        Tags tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

        expect(meterRegistry.counter("rabbit.listener.event", tags)).andReturn(mock(Counter.class));
        expect(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).andReturn(mock(Counter.class));

        replayAll();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        successRabbitListener.handle("", messageProperties);
        verifyAll();
    }

    @Test
    public void testHandleEventWithoutResponse()
    {
        Tags tags = Tags.of(Tag.of("listener", "FailureOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

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
        Tags tags = Tags.of(Tag.of("listener", "FailureOptionalSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

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
        Tags tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

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
        Tags tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

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

    private class SuccessOptionalSpringRabbitListener extends OptionalResponseSpringRabbitListener<Object>
    {
        public SuccessOptionalSpringRabbitListener()
        {
            super(responseRabbitTemplate);
        }

        @Override
        protected Object processRequest(Object requestObject)
        {
            return "response";
        }
    }

    private class FailureOptionalSpringRabbitListener extends OptionalResponseSpringRabbitListener<Object>
    {
        public FailureOptionalSpringRabbitListener()
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
