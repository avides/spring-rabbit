package com.avides.spring.rabbit.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@ExtendWith(MockitoExtension.class)
public class OptionalResponseSpringRabbitListenerTest
{
    private OptionalResponseSpringRabbitListener<Object> successRabbitListener;

    private OptionalResponseSpringRabbitListener<Object> failureRabbitListener;

    @Mock
    private RabbitTemplate responseRabbitTemplate;

    @Mock
    private MeterRegistry meterRegistry;

    @BeforeEach
    public void setup()
    {
        successRabbitListener = new SuccessOptionalSpringRabbitListener();
        ReflectionTestUtils.setField(successRabbitListener, "meterRegistry", meterRegistry);

        failureRabbitListener = new FailureOptionalSpringRabbitListener();
        ReflectionTestUtils.setField(failureRabbitListener, "meterRegistry", meterRegistry);
    }

    @Test
    public void testHandleEventWithoutReplyTo()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        successRabbitListener.handle("", messageProperties);
    }

    @Test
    public void testHandleEventWithoutResponse()
    {
        var tags = Tags.of(Tag.of("listener", "FailureOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
    }

    @Test
    public void testHandleEventWithoutResponseAndAppId()
    {
        var tags = Tags.of(Tag.of("listener", "FailureOptionalSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);
    }

    @Test
    public void testHandleEvent()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "sender-app"));

        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);

        verify(responseRabbitTemplate).convertAndSend(eq(""), eq("response-queue"), eq("response"), any(MessagePostProcessor.class));
    }

    @Test
    public void testHandleEventWithoutAppId()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessOptionalSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(mock(Counter.class));
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(mock(Counter.class));

        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);

        verify(responseRabbitTemplate).convertAndSend(eq(""), eq("response-queue"), eq("response"), any(MessagePostProcessor.class));
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
