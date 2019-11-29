package com.avides.spring.rabbit.configuration.creator;

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
        dlxArguments.put("x-queue-mode", "lazy");

        Queue dlxQueue = new Queue(queueProperties.getName() + ".dlx", queueProperties.isDurable(), queueProperties.isExclusive(), false, dlxArguments);
        dlxQueue.setAdminsThatShouldDeclare(rabbitAdmin);
        rabbitAdmin.declareQueue(dlxQueue);
        return dlxQueue;
    }

}
