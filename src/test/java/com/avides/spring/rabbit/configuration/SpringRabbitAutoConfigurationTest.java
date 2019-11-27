package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class SpringRabbitAutoConfigurationTest implements DomainTestSupport
{
    private ApplicationContextRunner contextRunner;

    @Before
    public void setUp()
    {
        contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(SpringRabbitAutoConfiguration.BeforePublishMessagePostProcessorAutoConfiguration.class));
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithExplicitAppIdEnricherMessagePostProcessorPropertyValue()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled=true").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdPropertyValue()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id=test").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("test")));
        });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdPropertyValueIsEmpty()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id=").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndArtifactIdPropertyValue()
    {
        contextRunner.withPropertyValues("info.artifactId=spring-rabbit").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("spring-rabbit")));
        });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndWithoutPropertyValue()
    {
        contextRunner.run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdAndArtifactIdPropertyValue()
    {
        contextRunner
                .withPropertyValues("info.artifactId=spring-rabbit", "spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id=test")
                .run(context ->
                {
                    assertThat(context).hasSingleBean(MessagePostProcessor.class);

                    Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

                    assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("test")));
                });
    }

    @Test
    public void testBeforePublishMessagePostProcessorAutoConfigurationWithDummyMessagePostProcessor()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled=false").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage());
        });
    }

    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteSpringRabbitAutoConfiguration());
    }

    // queues
    @Test
    public void testBeanValidationOnQueuesWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setQueues(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "queues");
    }

    @Test
    public void testBeanValidationOnQueuesWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getQueues().get(0).setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "queues");
    }

    // outbounds
    @Test
    public void testBeanValidationOnOutboundsWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setOutbounds(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "outbounds");
    }

    @Test
    public void testBeanValidationOnOutboundsWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getOutbounds().get(0).setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "outbounds");
    }

    // connections
    @Test
    public void testBeanValidationOnConnectionsWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setConnections(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    public void testBeanValidationOnConnectionsWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getConnections().get(0).setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "connections");
    }

    // exchange
    @Test
    public void testBeanValidationOnExchangeWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setExchange(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    public void testBeanValidationOnExchangeWithInvalid()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "exchange");
    }

    // messageConverter
    @Test
    public void testBeanValidationOnMessageConverterWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMessageConverter(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    public void testBeanValidationOnMessageConverterWithInvalid()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getMessageConverter().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "messageConverter");
    }

    // maxConcurrentConsumers
    @Test
    public void testBeanValidationOnMaxConcurrentConsumersWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMaxConcurrentConsumers(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "maxConcurrentConsumers");
    }

    @Test
    public void testBeanValidationOnMaxConcurrentConsumersWithLessThanOne()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMaxConcurrentConsumers(Integer.valueOf(0));
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "maxConcurrentConsumers");
    }

    // apiPort
    @Test
    public void testBeanValidationOnApiPortWithLessThanOne()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setApiPort(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "apiPort");
    }

    @Test
    public void testBeanValidationOnApiPortWithMoreThan65535()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setApiPort(65536);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "apiPort");
    }

    /*
     * test default values
     */
    @Test
    public void testDefaultValueOnQueues()
    {
        assertTrue(new SpringRabbitAutoConfiguration().getQueues().isEmpty());
    }

    @Test
    public void testDefaultValueOnOutbounds()
    {
        assertTrue(new SpringRabbitAutoConfiguration().getOutbounds().isEmpty());
    }

    @Test
    public void testDefaultValueOnConnections()
    {
        assertNull(new SpringRabbitAutoConfiguration().getConnections());
    }

    @Test
    public void testDefaultValueOnExchange()
    {
        assertNull(new SpringRabbitAutoConfiguration().getExchange());
    }

    @Test
    public void testDefaultValueOnMessageConverter()
    {
        assertNull(new SpringRabbitAutoConfiguration().getMessageConverter());
    }

    @Test
    public void testDefaultValueOnMaxConcurrentConsumers()
    {
        assertEquals(Integer.valueOf(1), new SpringRabbitAutoConfiguration().getMaxConcurrentConsumers());
    }

    @Test
    public void testDefaultValueOnApiPort()
    {
        assertEquals(15672, new SpringRabbitAutoConfiguration().getApiPort());
    }
}
