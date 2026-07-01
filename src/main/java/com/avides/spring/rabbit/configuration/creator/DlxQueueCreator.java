package com.avides.spring.rabbit.configuration.creator;

import static com.avides.spring.rabbit.configuration.creator.QueueCreator.DEFAULT_X_QUEUE_TYPE;
import static com.avides.spring.rabbit.configuration.creator.QueueCreator.X_QUEUE_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DlxQueueCreator implements Creator<Queue>
{
    private final RabbitAdmin rabbitAdmin;

    private final QueueProperties queueProperties;

    @Override
    public Queue createInstance()
    {
        Map<String, Object> dlxArguments = new HashMap<>();
        dlxArguments.put("x-max-length", Long.valueOf(queueProperties.getLimit()));
        dlxArguments.put(X_QUEUE_TYPE, resolveQueueType());

        Queue dlxQueue = new Queue(queueProperties.getName() + ".dlx", queueProperties.isDurable(), queueProperties.isExclusive(), false, dlxArguments);
        dlxQueue.setAdminsThatShouldDeclare(rabbitAdmin);
        rabbitAdmin.declareQueue(dlxQueue);
        return dlxQueue;
    }

    private String resolveQueueType()
    {
        if (queueProperties.getArguments() != null && queueProperties.getArguments().get(X_QUEUE_TYPE) != null)
        {
            return (String) queueProperties.getArguments().get(X_QUEUE_TYPE);
        }
        return DEFAULT_X_QUEUE_TYPE;
    }

}
