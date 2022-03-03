package com.avides.spring.rabbit.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.avides.spring.rabbit.configuration.creator.Creator;
import com.avides.spring.rabbit.configuration.creator.CustomConnectionFactoryCreator;
import com.avides.spring.rabbit.configuration.creator.DlxQueueCreator;
import com.avides.spring.rabbit.configuration.creator.ListenerCreator;
import com.avides.spring.rabbit.configuration.creator.QueueCreator;
import com.avides.spring.rabbit.configuration.creator.RabbitAdminCreator;
import com.avides.spring.rabbit.configuration.creator.RabbitTemplateCreator;
import com.avides.spring.rabbit.configuration.domain.BeanReferenceConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.domain.CustomConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.configuration.domain.ListenerProperties;
import com.avides.spring.rabbit.configuration.domain.MessageConverterProperties;
import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.configuration.domain.RabbitTemplateProperties;
import com.avides.spring.rabbit.configuration.provider.ConnectionFactoryProvider;
import com.avides.spring.rabbit.configuration.util.DefaultValueResolver;
import com.avides.spring.rabbit.configuration.util.MessagePostProcessorUtils;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring auto configuration for the communication with RabbitMQ.
 * <p>
 * In addition to the necessary configuration for sending and receiving (see at the README), some additional features are supported:
 * <ul>
 * <li>Metrics
 * <li>MessagePostProcessors
 * </ul>
 *
 */
@Validated
@Conditional(IsSpringRabbitEnabled.class)
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Getter
@Setter
@Slf4j
public class SpringRabbitAutoConfiguration implements InitializingBean
{
    private static final String CONNECTION_FACTORY_BEAN_NAME = "springRabbitConnectionFactory";

    @Autowired(required = false)
    private ConnectionFactory connectionFactory;

    @Autowired(required = false)
    private RabbitProperties rabbitProperties;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private List<MessageConverter> existingMessageConverterList;

    @Autowired
    private List<MessagePostProcessor> messagePostProcessors;

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private List<QueueProperties> queues = new ArrayList<>();

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private List<RabbitTemplateProperties> outbounds = new ArrayList<>();

    @Valid
    @NestedConfigurationProperty
    private List<CustomConnectionFactoryProperties> connections;

    @Valid
    @NestedConfigurationProperty
    private ExchangeProperties exchange;

    @Valid
    @NestedConfigurationProperty
    private MessageConverterProperties messageConverter;

    @NotNull
    @Min(1)
    private Integer prefetchCount = Integer.valueOf(500);

    @NotNull
    @Min(1)
    private Integer maxConcurrentConsumers = Integer.valueOf(1);

    @Range(min = 1, max = 65535)
    private int apiPort = 15672;

    /**
     * Auto configuration for custom {@link MessagePostProcessor}s that are added to the {@link RabbitTemplate}.
     * <p>
     * If there are some {@link MessagePostProcessor}s that can not be used as a bean (e.g. through the usage of rabbit template properties), they will be
     * created by {@link MessagePostProcessorUtils}.
     *
     * @see RabbitTemplate#addBeforePublishPostProcessors(MessagePostProcessor...)
     * @see RabbitTemplateCreator#createInstance()
     * @see MessagePostProcessorUtils
     */
    @Configuration
    protected static class BeforePublishMessagePostProcessorAutoConfiguration
    {
        /**
         * {@link MessagePostProcessor} that adds the configured app-id as {@link MessageProperties#setAppId(String)}.
         * <p>
         * Prefers the value of <code>environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id")</code> as
         * app-id. If no property value exists, <code>environment.getProperty("info.artifactId", "UNKNOWN")</code> is used.
         * <p>
         * Could be disabled by setting <code>spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled</code> to false.
         *
         * @param environment {@link Environment} to resolve necessary application properties
         * @return The enriching {@link MessagePostProcessor}
         */
        @Bean
        @ConditionalOnProperty(name = "spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled", havingValue = "true", matchIfMissing = true)
        public MessagePostProcessor appIdEnricherMessagePostProcessor(Environment environment)
        {
            String appId = environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id");

            if (StringUtils.isEmpty(appId))
            {
                appId = environment.getProperty("info.artifactId", "UNKNOWN");
            }

            // Local variable appId defined in an enclosing scope must be final or effectively final
            final String resolvedAppId = appId;

            return message ->
            {
                message.getMessageProperties().setAppId(resolvedAppId);
                return message;
            };
        }

        /**
         * Dummy {@link MessagePostProcessor} that is used if no other bean exists to simplify the handling.
         *
         * @return The dummy {@link MessagePostProcessor}
         */
        @Bean
        @ConditionalOnMissingBean
        public MessagePostProcessor dummyMessagePostProcessor()
        {
            return message -> message;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        configureConnectionFactories();
        configureQueues();
        configureRabbitTemplates();
    }

    private void configureConnectionFactories()
    {
        if (!CollectionUtils.isEmpty(connections))
        {
            connections.forEach(connectionFactoryProperties ->
            {
                Creator<ConnectionFactory> connectionFactoryCreator = new CustomConnectionFactoryCreator(connectionFactoryProperties);
                applicationContext.registerBean(connectionFactoryProperties
                        .getBeanName(), ConnectionFactory.class, connectionFactoryCreator::createInstance, beanDefinition -> beanDefinition
                                .setScope(BeanDefinition.SCOPE_SINGLETON));
                log.info("ConnectionFactory build with bean-name '{}'", connectionFactoryProperties.getBeanName());
            });
        }
        else if (connectionFactory != null)
        {
            ConnectionFactory convertedConnectionFactory = ConnectionFactoryProvider.createConnectionFactory(connectionFactory, rabbitProperties, apiPort);
            applicationContext.registerBean(CONNECTION_FACTORY_BEAN_NAME, ConnectionFactory.class, () -> convertedConnectionFactory, beanDefinition ->
            {
                beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                beanDefinition.setPrimary(true);
            });
            log.info("ConnectionFactory converted to possible queueMasterLocatorConnectionFactory with bean name '{}'", CONNECTION_FACTORY_BEAN_NAME);
        }
        else
        {
            throw new IllegalArgumentException("No connection factory configured. Either configure the connection factory of Spring or declare some custom connection factories.");
        }
    }

    private void configureQueues()
    {
        queues.forEach(queueProperties ->
        {
            if (queueProperties.isCreationEnabled())
            {
                var queueName = queueProperties.getName();
                var rabbitAdminProperties = queueProperties.getRabbitAdmin();
                var rabbitAdminBeanName = rabbitAdminProperties.getBeanName();
                var customConnectionFactoryProperties = rabbitAdminProperties.getConnectionFactory();

                var resolvedConnectionFactory = DefaultValueResolver
                        .resolveConnectionFactory(customConnectionFactoryProperties, CONNECTION_FACTORY_BEAN_NAME, applicationContext);

                var rabbitAdmin = new RabbitAdminCreator(resolvedConnectionFactory).createInstance();
                if (!applicationContext.containsBean(rabbitAdminBeanName))
                {
                    applicationContext.registerBean(rabbitAdminBeanName, RabbitAdmin.class, () -> rabbitAdmin, beanDefinition -> beanDefinition
                            .setScope(BeanDefinition.SCOPE_SINGLETON));
                    log.info("RabbitAdmin build with bean-name '{}'", rabbitAdminBeanName);
                }

                var queueCreator = new QueueCreator(queueProperties, rabbitAdmin, DefaultValueResolver
                        .resolveExchange(queueProperties.getExchange(), exchange));
                addToContext(queueName, resolvedConnectionFactory, queueCreator.createInstance());

                var dlxQueueCreator = new DlxQueueCreator(rabbitAdmin, queueProperties);
                addToContext(queueName + ".dlx", resolvedConnectionFactory, dlxQueueCreator.createInstance());

                if (queueProperties.getListener() != null)
                {
                    var listenerProperties = queueProperties.getListener();

                    if (listenerProperties.isCreationEnabled())
                    {
                        var listenerBeanName = listenerProperties.getBeanName();
                        var resolvedPrefetchCount = DefaultValueResolver.resolveValue(listenerProperties.getPrefetchCount(), prefetchCount);
                        var resolvedMaxConcurrentConsumers = DefaultValueResolver
                                .resolveValue(listenerProperties.getMaxConcurrentConsumers(), maxConcurrentConsumers);
                        var resolvedMessageConverter = DefaultValueResolver.resolveMessageConverter(listenerProperties
                                .getMessageConverter(), messageConverter, applicationContext, existingMessageConverterList);
                        var listener = applicationContext.getBean(listenerBeanName);

                        var listenerCreator = new ListenerCreator(resolvedConnectionFactory, queueName, resolvedPrefetchCount, resolvedMaxConcurrentConsumers, resolvedMessageConverter, listener);

                        var beanName = createListenerContainerBeanName(listenerProperties, queueName, customConnectionFactoryProperties);

                        applicationContext
                                .registerBean(beanName, DefaultMessageListenerContainer.class, listenerCreator::createInstance, beanDefinition -> beanDefinition
                                        .setScope(BeanDefinition.SCOPE_SINGLETON));
                        log.info("MessageListenerContainer build with bean-name '{}'", beanName);
                    }
                    else
                    {
                        log.info("Listener creation disabled (name: {})", listenerProperties.getBeanName());
                    }
                }
            }
            else
            {
                log.info("Queue creation disabled (name: {})", queueProperties.getName());
            }
        });
    }

    private void addToContext(String name, ConnectionFactory resolvedConnectionFactory, Queue queue)
    {
        String beanName = name + "_" + resolvedConnectionFactory.getHost();
        applicationContext.registerBean(beanName, Queue.class, () -> queue, beanDefinition -> beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON));
        log.info("Queue build with bean-name '{}'", beanName);
    }

    private void configureRabbitTemplates()
    {
        outbounds.forEach(rabbitTemplateProperties ->
        {
            String rabbitTemplateBeanName = rabbitTemplateProperties.getBeanName();
            Exchange resolvedExchange = DefaultValueResolver.resolveExchange(rabbitTemplateProperties.getExchange(), exchange);
            ConnectionFactory resolvedConnectionFactory = DefaultValueResolver
                    .resolveConnectionFactory(rabbitTemplateProperties.getConnectionFactory(), CONNECTION_FACTORY_BEAN_NAME, applicationContext);
            MessageConverter resolvedMessageConverter = DefaultValueResolver.resolveMessageConverter(rabbitTemplateProperties
                    .getMessageConverter(), messageConverter, applicationContext, existingMessageConverterList);

            List<MessagePostProcessor> totalMessagePostProcessors = resolveTotalMessagePostProcessors(rabbitTemplateBeanName);

            Creator<RabbitTemplate> rabbitTemplateCreator = new RabbitTemplateCreator(resolvedConnectionFactory, resolvedExchange, resolvedMessageConverter, rabbitTemplateProperties, totalMessagePostProcessors);
            RabbitTemplate rabbitTemplate = rabbitTemplateCreator.createInstance();
            applicationContext.registerBean(rabbitTemplateBeanName, RabbitTemplate.class, () -> rabbitTemplate, beanDefinition -> beanDefinition
                    .setScope(BeanDefinition.SCOPE_SINGLETON));
            log.info("RabbitTemplate build with autowiredName " + rabbitTemplateBeanName);
        });
    }

    private List<MessagePostProcessor> resolveTotalMessagePostProcessors(String rabbitTemplateBeanName)
    {
        List<MessagePostProcessor> totalMessagePostProcessors = new ArrayList<>(messagePostProcessors);
        totalMessagePostProcessors.addAll(MessagePostProcessorUtils
                .resolveAdditionalMessagePostProcessors(applicationContext.getEnvironment(), meterRegistry, rabbitTemplateBeanName));
        return totalMessagePostProcessors;
    }

    private static String createListenerContainerBeanName(ListenerProperties listenerProperties, String queueName,
            BeanReferenceConnectionFactoryProperties connectionFactoryProperties)
    {
        String connectionFactoryBeanName = DefaultValueResolver.resolveConnectionFactoryBeanName(connectionFactoryProperties, CONNECTION_FACTORY_BEAN_NAME);
        return listenerProperties.getBeanName() + "@" + queueName + "@" + connectionFactoryBeanName;
    }
}
