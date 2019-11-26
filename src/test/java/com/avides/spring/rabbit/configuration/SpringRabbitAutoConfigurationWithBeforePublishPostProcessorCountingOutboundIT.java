package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.TestClass;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;

@ActiveProfiles(profiles = { "it", "beforePublishPostProcessor" })
public class SpringRabbitAutoConfigurationWithBeforePublishPostProcessorCountingOutboundIT extends AbstractIT
{
    @Autowired
    private RabbitTemplate testSendRabbitTemplate;

    @Autowired
    private RabbitTemplate testSendOtherRabbitTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Before
    @After
    public void clearInbounds()
    {
        meterRegistry.getMeters().forEach(m -> meterRegistry.remove(m));
    }

    @Test
    public void testCountingOutbound()
    {
        testSendOtherRabbitTemplate.convertAndSend(TestClass.buildBase());
        testSendOtherRabbitTemplate.convertAndSend(TestClass.buildBase());
        testSendRabbitTemplate.convertAndSend(TestClass.buildBase());

        assertThat(meterRegistry.find("rabbit.outbound.message")).isNotNull();
        assertThat(getCounterSum(meterRegistry.find("rabbit.outbound.message"))).isEqualTo(3);
        assertThat(getCounterSum(meterRegistry.find("rabbit.outbound.message").tag("template", "testSendRabbitTemplate"))).isEqualTo(1);
        assertThat(getCounterSum(meterRegistry.find("rabbit.outbound.message").tag("template", "testSendOtherRabbitTemplate"))).isEqualTo(2);
    }

    private static double getCounterSum(Search search)
    {
        return new ArrayList<>(search.counters()).stream().mapToDouble(e -> e.count()).sum();
    }
}
