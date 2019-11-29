package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.TestClass;
import com.avides.spring.rabbit.test.support.TestClassContextAwareListener;

@ActiveProfiles({ "it", "beforePublishPostProcessor", "beforePublishPostProcessorAppIdEnricherWithConfiguredAppIdByArtifactId" })
public class SpringRabbitAutoConfigurationWithBeforePublishPostProcessorAppIdEnricherWithConfiguredAppIdByArtifactIdIT extends AbstractIT
{
    @Autowired
    private TestClassContextAwareListener testListener;

    @Autowired
    private RabbitTemplate testSendRabbitTemplate;

    @Before
    @After
    public void clearInbounds()
    {
        testListener.getInbounds().clear();
    }

    @Test
    public void testAppIdEnricher()
    {
        testSendRabbitTemplate.convertAndSend(TestClass.buildBase());

        await().untilAsserted(() ->
        {
            assertThat(testListener.getInbounds()).hasSize(1);
            assertThat(testListener.getInbounds().get(0).getMessageProperties().getAppId()).isEqualTo("test-artifact");
        });
    }
}
