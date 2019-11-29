package com.avides.spring.rabbit.configuration.util;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import com.avides.spring.rabbit.utils.DomainTestSupport;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RunWith(PowerMockRunner.class)
public class MessagePostProcessorUtilsTest implements DomainTestSupport
{
    @MockStrict
    private ConfigurableEnvironment environment;

    @MockStrict
    private MeterRegistry meterRegistry;

    @MockStrict
    private Counter counter;

    @Test
    public void testResolveAdditionalMessagePostProcessorsWithCountingOutbound()
    {
        environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled", Boolean.class, TRUE);
        expectLastCall().andReturn(Boolean.TRUE);

        meterRegistry.counter("rabbit.outbound.message", "template", "test");
        expectLastCall().andReturn(counter);

        counter.increment();

        replayAll();
        List<MessagePostProcessor> messagePostProcessors = MessagePostProcessorUtils.resolveAdditionalMessagePostProcessors(environment, meterRegistry, "test");
        // test message post processor
        messagePostProcessors.get(0).postProcessMessage(getDummyMessage());
        verifyAll();

        assertThat(messagePostProcessors).hasSize(1);
    }

    @Test
    public void testResolveAdditionalMessagePostProcessorsWithCountingOutboundisDisabled()
    {
        environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled", Boolean.class, TRUE);
        expectLastCall().andReturn(Boolean.FALSE);

        replayAll();
        List<MessagePostProcessor> messagePostProcessors = MessagePostProcessorUtils.resolveAdditionalMessagePostProcessors(environment, meterRegistry, "test");
        verifyAll();

        assertThat(messagePostProcessors).isEmpty();
    }
}
