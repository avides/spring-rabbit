package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.TestClass;

import io.micrometer.core.instrument.MeterRegistry;

@ActiveProfiles(profiles = { "it", "beforePublishPostProcessor", "beforePublishPostProcessorCountingOutboundIsDisabled" })
public class SpringRabbitAutoConfigurationWithBeforePublishPostProcessorCountingOutboundIsDisabledIT extends AbstractIT
{
    @Autowired
    private RabbitTemplate testSendRabbitTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Before
    @After
    public void clearInbounds()
    {
        meterRegistry.getMeters().forEach(m -> assertNotNull(meterRegistry.remove(m)));
    }

    @Test
    public void testCountingOutbound()
    {
        testSendRabbitTemplate.convertAndSend(TestClass.buildBase());

        assertThat(meterRegistry.find("rabbit.outbound.message").counter()).isNull();
    }
}
