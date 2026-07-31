package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;

import com.avides.spring.rabbit.configuration.domain.ListenerProperties;
import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class SpringRabbitAutoConfigurationTest implements DomainTestSupport
{
    private static SpringRabbitAutoConfiguration newAutoConfiguration()
    {
        return new SpringRabbitAutoConfiguration(null, null, null, null, List.of(), List.of());
    }

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp()
    {
        contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(SpringRabbitAutoConfiguration.BeforePublishMessagePostProcessorAutoConfiguration.class));
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithExplicitAppIdEnricherMessagePostProcessorPropertyValue()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled=true").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdPropertyValue()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id=test").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("test")));
        });
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdPropertyValueIsEmpty()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id=").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndArtifactIdPropertyValue()
    {
        contextRunner.withPropertyValues("info.artifactId=spring-rabbit").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("spring-rabbit")));
        });
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndWithoutPropertyValue()
    {
        contextRunner.run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage(m -> m.getMessageProperties().setAppId("UNKNOWN")));
        });
    }

    @Test
    void testBeforePublishMessagePostProcessorAutoConfigurationWithAppIdEnricherMessagePostProcessorAndAppIdAndArtifactIdPropertyValue()
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
    void testBeforePublishMessagePostProcessorAutoConfigurationWithDummyMessagePostProcessor()
    {
        contextRunner.withPropertyValues("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled=false").run(context ->
        {
            assertThat(context).hasSingleBean(MessagePostProcessor.class);

            Message processed = context.getBean(MessagePostProcessor.class).postProcessMessage(getDummyMessage());

            assertThat(processed).isEqualTo(getDummyMessage());
        });
    }

    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteSpringRabbitAutoConfiguration());
    }

    // queues
    @Test
    void testBeanValidationOnQueuesWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setQueues(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "queues");
    }

    @Test
    void testBeanValidationOnQueuesWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getQueues().get(0).setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "queues");
    }

    // outbounds
    @Test
    void testBeanValidationOnOutboundsWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setOutbounds(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "outbounds");
    }

    @Test
    void testBeanValidationOnOutboundsWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getOutbounds().get(0).setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "outbounds");
    }

    // connections
    @Test
    void testBeanValidationOnConnectionsWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setConnections(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    void testBeanValidationOnConnectionsWithInvalidEntry()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getConnections().get(0).setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "connections");
    }

    // exchange
    @Test
    void testBeanValidationOnExchangeWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setExchange(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    void testBeanValidationOnExchangeWithInvalid()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "exchange");
    }

    // messageConverter
    @Test
    void testBeanValidationOnMessageConverterWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMessageConverter(null);
        BeanValidationTestSupport.expectNoError(configuration);
    }

    @Test
    void testBeanValidationOnMessageConverterWithInvalid()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.getMessageConverter().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "messageConverter");
    }

    // maxConcurrentConsumers
    @Test
    void testBeanValidationOnMaxConcurrentConsumersWithNull()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMaxConcurrentConsumers(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "maxConcurrentConsumers");
    }

    @Test
    void testBeanValidationOnMaxConcurrentConsumersWithLessThanOne()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setMaxConcurrentConsumers(Integer.valueOf(0));
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "maxConcurrentConsumers");
    }

    // apiPort
    @Test
    void testBeanValidationOnApiPortWithLessThanOne()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setApiPort(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "apiPort");
    }

    @Test
    void testBeanValidationOnApiPortWithMoreThan65535()
    {
        SpringRabbitAutoConfiguration configuration = getCompleteSpringRabbitAutoConfiguration();
        configuration.setApiPort(65536);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(configuration, "apiPort");
    }

    /*
     * test default values
     */
    @Test
    void testDefaultValueOnQueues()
    {
        assertTrue(newAutoConfiguration().getQueues().isEmpty());
    }

    @Test
    void testDefaultValueOnOutbounds()
    {
        assertTrue(newAutoConfiguration().getOutbounds().isEmpty());
    }

    @Test
    void testDefaultValueOnConnections()
    {
        assertNull(newAutoConfiguration().getConnections());
    }

    @Test
    void testDefaultValueOnExchange()
    {
        assertNull(newAutoConfiguration().getExchange());
    }

    @Test
    void testDefaultValueOnMessageConverter()
    {
        assertNull(newAutoConfiguration().getMessageConverter());
    }

    @Test
    void testDefaultValueOnMaxConcurrentConsumers()
    {
        assertEquals(Integer.valueOf(1), newAutoConfiguration().getMaxConcurrentConsumers());
    }

    @Test
    void testDefaultValueOnApiPort()
    {
        assertEquals(15672, newAutoConfiguration().getApiPort());
    }

    /*
     * test listener container registration
     */
    @Test
    void testListenerContainerIsRegisteredAsSingletonWithGenericTargetType() throws Exception
    {
        Channel channel = mock(Channel.class);
        when(channel.queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyMap())).thenReturn(mock(DeclareOk.class));

        Connection connection = mock(Connection.class);
        when(connection.createChannel(false)).thenReturn(channel);

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(connectionFactory.getHost()).thenReturn("localhost");
        when(connectionFactory.createConnection()).thenReturn(connection);

        try (GenericApplicationContext applicationContext = new GenericApplicationContext())
        {
            applicationContext.registerBean("testListener", Object.class);
            applicationContext.refresh();

            createAutoConfigurationForSingleQueue(applicationContext, connectionFactory).afterPropertiesSet();

            BeanDefinition beanDefinition = applicationContext.getBeanDefinition("testListener@testQueueName@springRabbitConnectionFactory");

            assertEquals(BeanDefinition.SCOPE_SINGLETON, beanDefinition.getScope());
            assertEquals(ResolvableType.forClassWithGenerics(DefaultMessageListenerContainer.class, Object.class), beanDefinition.getResolvableType());
        }
    }

    private SpringRabbitAutoConfiguration createAutoConfigurationForSingleQueue(GenericApplicationContext applicationContext,
            ConnectionFactory connectionFactory)
    {
        ListenerProperties listenerProperties = new ListenerProperties();
        listenerProperties.setBeanName("testListener");

        QueueProperties queueProperties = new QueueProperties();
        queueProperties.setName("testQueueName");
        queueProperties.setRoutingkey("product");
        queueProperties.setLimit(100);
        queueProperties.setListener(listenerProperties);

        SpringRabbitAutoConfiguration autoConfiguration = new SpringRabbitAutoConfiguration(connectionFactory, getCompleteRabbitProperties(), null,
                applicationContext, List.of(new SimpleMessageConverter()), List.of());
        autoConfiguration.setExchange(getCompleteExchangeProperties());
        autoConfiguration.setQueues(List.of(queueProperties));
        return autoConfiguration;
    }
}
