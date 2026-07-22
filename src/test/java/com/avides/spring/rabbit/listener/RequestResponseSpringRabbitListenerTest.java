package com.avides.spring.rabbit.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
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
public class RequestResponseSpringRabbitListenerTest
{
    private RequestResponseSpringRabbitListener<Object> successRabbitListener;

    private RequestResponseSpringRabbitListener<Object> failureRabbitListener;

    @Mock
    private RabbitTemplate responseRabbitTemplate;

    @Mock
    private MeterRegistry meterRegistry;

    @BeforeEach
    public void setup()
    {
        successRabbitListener = new SuccessRequestResponseSpringRabbitListener();
        ReflectionTestUtils.setField(successRabbitListener, "meterRegistry", meterRegistry);

        failureRabbitListener = new FailureRequestResponseSpringRabbitListener();
        ReflectionTestUtils.setField(failureRabbitListener, "meterRegistry", meterRegistry);
    }

    @Test
    public void testHandleEventWithoutCorrelationId()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);

        verify(responseRabbitTemplate).convertAndSend(eq(""), eq("response-queue"), eq("response"), any(MessagePostProcessor.class));
        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleEventWithoutReplyTo()
    {
        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> successRabbitListener.handleEvent("", messageProperties));
        assertEquals("reply_to must not be null", e.getMessage());
    }

    @Test
    public void testHandleEventWithoutResponse()
    {
        var tags = Tags.of(Tag.of("listener", "FailureRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleEventWithoutResponseAndAppId()
    {
        var tags = Tags.of(Tag.of("listener", "FailureRequestResponseSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        failureRabbitListener.handle("", messageProperties);

        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleEvent()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "sender-app"));

        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setAppId("sender-app");
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);

        verify(responseRabbitTemplate).convertAndSend(eq(""), eq("response-queue"), eq("response"), any(MessagePostProcessor.class));
        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
    }

    @Test
    public void testHandleEventWithoutAppId()
    {
        var tags = Tags.of(Tag.of("listener", "SuccessRequestResponseSpringRabbitListener"), Tag.of("from", "UNKNOWN"));

        Counter eventCounter = mock(Counter.class);
        Counter durationCounter = mock(Counter.class);
        when(meterRegistry.counter("rabbit.listener.event", tags)).thenReturn(eventCounter);
        when(meterRegistry.counter("rabbit.listener.event.total.duration.milliseconds", tags)).thenReturn(durationCounter);

        var messageProperties = new MessageProperties();
        messageProperties.setCorrelationId("request1");
        messageProperties.setReplyTo("response-queue");
        successRabbitListener.handle("", messageProperties);

        verify(responseRabbitTemplate).convertAndSend(eq(""), eq("response-queue"), eq("response"), any(MessagePostProcessor.class));
        verify(eventCounter).increment();
        verify(durationCounter).increment(anyDouble());
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
