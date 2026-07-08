package com.avides.spring.rabbit.configuration.util;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import com.avides.spring.rabbit.utils.DomainTestSupport;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
public class MessagePostProcessorUtilsTest implements DomainTestSupport
{
    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Test
    public void testResolveAdditionalMessagePostProcessorsWithCountingOutbound()
    {
        when(environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled", Boolean.class, TRUE))
                .thenReturn(Boolean.TRUE);

        when(meterRegistry.counter("rabbit.outbound.message", "template", "test")).thenReturn(counter);

        List<MessagePostProcessor> messagePostProcessors = MessagePostProcessorUtils.resolveAdditionalMessagePostProcessors(environment, meterRegistry, "test");
        // test message post processor
        messagePostProcessors.get(0).postProcessMessage(getDummyMessage());

        verify(counter).increment();
        assertThat(messagePostProcessors).hasSize(1);
    }

    @Test
    public void testResolveAdditionalMessagePostProcessorsWithCountingOutboundisDisabled()
    {
        when(environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled", Boolean.class, TRUE))
                .thenReturn(Boolean.FALSE);

        List<MessagePostProcessor> messagePostProcessors = MessagePostProcessorUtils.resolveAdditionalMessagePostProcessors(environment, meterRegistry, "test");

        assertThat(messagePostProcessors).isEmpty();
    }
}
