package com.avides.spring.rabbit.configuration;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Checks if any connection is available
 *
 */
public class IsSpringRabbitEnabled extends AnyNestedCondition
{
    /**
     * Constructs check
     */
    public IsSpringRabbitEnabled()
    {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty("spring.rabbitmq.addresses")
    static class HasSingleConnection
    {
    }

    @ConditionalOnProperty("spring.rabbitmq.connections[0].addresses")
    static class HasMultipleConnections
    {
    }
}
