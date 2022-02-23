package com.avides.spring.rabbit.configuration.util;

import java.util.List;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.avides.spring.rabbit.configuration.creator.ExchangeCreator;
import com.avides.spring.rabbit.configuration.domain.BeanReferenceConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.configuration.domain.MessageConverterProperties;
import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultValueResolver
{
    /**
     * Resolves and creates a {@link Exchange} for a custom and a default exchange configuration considering the override of a possible custom configuration.
     *
     * @param customProperties custom exchange configuration that overrides the default
     * @param defaultProperties default exchange configuration that is used if no custom configuration exists
     * @return resolved {@link Exchange}
     */
    public Exchange resolveExchange(ExchangeProperties customProperties, ExchangeProperties defaultProperties)
    {
        if (customProperties != null)
        {
            return new ExchangeCreator(customProperties).createInstance();
        }
        else if (defaultProperties != null)
        {
            return new ExchangeCreator(defaultProperties).createInstance();
        }

        throw new IllegalArgumentException("Could not resolve exchange");
    }

    /**
     * Resolves a {@link ConnectionFactory} for multiple connection factories considering the override of a possible custom connection factory.
     *
     * @param customProperties {@link BeanReferenceConnectionFactoryProperties} containing the configuration for a custom connection factory that overrides the
     *        default
     * @param defaultBeanName bean name for the default connection factory (created by spring and renamed by the configuration)
     * @param applicationContext context containing all spring beans
     * @return resolved {@link ConnectionFactory}
     */
    public ConnectionFactory resolveConnectionFactory(BeanReferenceConnectionFactoryProperties customProperties, String defaultBeanName,
            GenericApplicationContext applicationContext)
    {
        if (customProperties != null)
        {
            ConnectionFactory customConnectionFactory = applicationContext.getBean(customProperties.getBeanName(), ConnectionFactory.class);
            Assert.notNull(customConnectionFactory, "QueueMasterLocatorConnectionFactory not found (" + customProperties + ")");
            return customConnectionFactory;
        }

        if (StringUtils.hasText(defaultBeanName))
        {
            ConnectionFactory defaultConnectionFactory = applicationContext.getBean(defaultBeanName, ConnectionFactory.class);
            Assert.notNull(defaultConnectionFactory, "DefaultConnectionFactory not found (beanName:" + defaultBeanName + ")");
            return defaultConnectionFactory;
        }

        throw new IllegalArgumentException("Could not resolve the connectionFactory");
    }

    /**
     * Resolves a bean name for multiple connection factories considering the override of a possible custom connection factory.
     *
     * @param customProperties {@link BeanReferenceConnectionFactoryProperties} containing the bean name for a custom connection factory that overrides the
     *        default
     * @param defaultBeanName bean name for the default connection factory (created by spring and renamed by the configuration) that is used if no custom
     * @return beanName for the resolved connectionFactory
     */
    public String resolveConnectionFactoryBeanName(BeanReferenceConnectionFactoryProperties customProperties, String defaultBeanName)
    {
        if (customProperties != null)
        {
            return customProperties.getBeanName();
        }

        return defaultBeanName;
    }

    /**
     * Resolves the max concurrent consumers for a {@link DefaultMessageListenerContainer} considering the override of a possible custom configuration.
     *
     * @deprecated Please use {@link #resolveValue(Integer, Integer)} instead
     * @param customMax custom quantity to override the default
     * @param defaultMax quantity which is used if no custom configuration exists
     * @return resolved max concurrent consumers for a {@link DefaultMessageListenerContainer}
     */
    @Deprecated(forRemoval = true, since = "2.6.0")
    public int resolveMaxConcurrentConsumers(Integer customMax, Integer defaultMax)
    {
        return resolveValue(customMax, defaultMax);
    }

    /**
     * Resolves the value between a custom and a default value
     *
     * @param customValue custom value to override the default
     * @param defaultValue value which is used if no custom configuration exists
     * @return resolved max
     */
    public int resolveValue(Integer customValue, Integer defaultValue)
    {
        if (customValue != null)
        {
            return customValue.intValue();
        }

        return defaultValue.intValue();
    }

    /**
     * Resolves a {@link MessageConverter} considering the override of a possible custom configuration and possible existing message converters in the context.
     * <br>
     *
     * <ol>
     * <li>custom</li>
     * <li>default</li>
     * <li>existing if only one {@link MessageConverter} exists</li>
     * </ol>
     *
     * @param customProperties custom configuration to override the default
     * @param defaultProperties default configuration which is used if no custom configuration exists
     * @param applicationContext context containing all spring beans
     * @param existingMessageConverters list of existing {@link MessageConverter}
     * @return resolved {@link MessageConverter}
     */
    public MessageConverter resolveMessageConverter(MessageConverterProperties customProperties, MessageConverterProperties defaultProperties,
            GenericApplicationContext applicationContext, List<MessageConverter> existingMessageConverters)
    {
        if (customProperties != null)
        {
            MessageConverter customMessageConverter = applicationContext.getBean(customProperties.getBeanName(), MessageConverter.class);
            Assert.notNull(customMessageConverter, "CustomMessageConverter not found (" + customProperties + ")");
            return customMessageConverter;
        }
        else if (defaultProperties != null)
        {
            MessageConverter defaultMessageConverter = applicationContext.getBean(defaultProperties.getBeanName(), MessageConverter.class);
            Assert.notNull(defaultMessageConverter, "DefaultMessageConverter not found (" + defaultProperties + ")");
            return defaultMessageConverter;
        }
        else if (existingMessageConverters.size() == 1)
        {
            return existingMessageConverters.get(0);
        }
        throw new IllegalArgumentException("Could not resolve messageConverter (existingMessageConverter size " + existingMessageConverters.size() + "))");
    }
}
