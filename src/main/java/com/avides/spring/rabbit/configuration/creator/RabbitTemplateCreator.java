package com.avides.spring.rabbit.configuration.creator;

import java.util.List;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import com.avides.spring.rabbit.configuration.domain.RabbitTemplateProperties;

import lombok.RequiredArgsConstructor;

/**
 * {@link Creator} for for the {@link RabbitTemplate} instances.
 */
@RequiredArgsConstructor
public class RabbitTemplateCreator implements Creator<RabbitTemplate>
{
    private final ConnectionFactory connectionFactory;

    private final Exchange exchange;

    private final MessageConverter messageConverter;

    private final RabbitTemplateProperties rabbitTemplateProperties;

    private final List<MessagePostProcessor> beforePublishPostProcessors;

    @Override
    public RabbitTemplate createInstance()
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        new RabbitAdmin(connectionFactory).declareExchange(exchange);
        rabbitTemplate.setExchange(exchange.getName());
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setRoutingKey(rabbitTemplateProperties.getRoutingkey());
        rabbitTemplate.addBeforePublishPostProcessors(beforePublishPostProcessors.toArray(new MessagePostProcessor[beforePublishPostProcessors.size()]));
        return rabbitTemplate;
    }
}
