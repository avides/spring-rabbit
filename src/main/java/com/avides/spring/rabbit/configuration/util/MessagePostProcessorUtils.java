package com.avides.spring.rabbit.configuration.util;

import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.experimental.UtilityClass;

/**
 * Utility class that resolves some additional {@link MessagePostProcessor}s that can not declared as a bean.
 *
 * @see #resolveAdditionalMessagePostProcessors(ConfigurableEnvironment, MeterRegistry, String)
 */
@UtilityClass
public class MessagePostProcessorUtils
{
    /**
     * Resolves additional {@link MessagePostProcessor}s.
     * <ul>
     * <li>counting-outbound
     * <ul>
     * <li>Enabled if <code>spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled</code> is true or not specified
     * <li>Using {@link MeterRegistry} will increment a counter with the bean name of the template as a tag for each message
     * </ul>
     * </ul>
     *
     * @param environment Used for resolving the configuration for each additional message post processor
     * @param meterRegistry Used for adding some metrics for each outbound
     * @param rabbitTemplateBeanName The bean name of the rabbit template
     * @return List of enabled additional {@link MessagePostProcessor}s
     */
    public List<MessagePostProcessor> resolveAdditionalMessagePostProcessors(ConfigurableEnvironment environment, MeterRegistry meterRegistry,
            String rabbitTemplateBeanName)
    {
        List<MessagePostProcessor> messagePostProcessors = new ArrayList<>();

        if (environment.getProperty("spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled", Boolean.class, TRUE)
                .booleanValue())
        {
            messagePostProcessors.add(message ->
            {
                meterRegistry.counter("rabbit.outbound.message", "template", rabbitTemplateBeanName).increment();
                return message;
            });
        }

        return messagePostProcessors;
    }
}
