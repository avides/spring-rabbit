package com.avides.spring.rabbit.configuration.creator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueCreator implements Creator<Queue>
{
    private final QueueProperties queueProperties;

    private final RabbitAdmin rabbitAdmin;

    private final Exchange exchange;

    @Override
    public Queue createInstance()
    {
        List<String> routingKeys = createRoutingKeys();
        Map<String, Object> arguments = createArguments();

        Queue queue = new Queue(queueProperties.getName(), queueProperties.isDurable(), queueProperties.isExclusive(), false, arguments);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        routingKeys.forEach(routingKey -> rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs()));
        return queue;
    }

    private Map<String, Object> createArguments()
    {
        Map<String, Object> arguments = queueProperties.getArguments();
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", queueProperties.getName() + ".dlx");
        arguments.put("x-max-length", Long.valueOf(queueProperties.getLimit()));
        return arguments;
    }

    private List<String> createRoutingKeys()
    {
        List<String> routingKeys = new ArrayList<>();

        if (StringUtils.hasText(queueProperties.getRoutingkey()))
        {
            checkRoutingKey(queueProperties.getRoutingkey());
            routingKeys.add(queueProperties.getRoutingkey());
        }
        else if (!CollectionUtils.isEmpty(queueProperties.getRoutingkeys()))
        {
            queueProperties.getRoutingkeys().forEach(routingKey ->
            {
                checkRoutingKey(routingKey);
                routingKeys.add(routingKey);
            });
        }
        else
        {
            throw new IllegalArgumentException("Either declare routing-keys or routing-key");
        }
        return routingKeys;
    }

    private static boolean checkRoutingKey(String routingkey)
    {
        if (!StringUtils.hasText(routingkey))
        {
            throw new IllegalArgumentException("Invalid routing-key");
        }

        return true;
    }
}
