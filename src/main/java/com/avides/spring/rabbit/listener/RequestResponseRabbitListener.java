package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;

public abstract class RequestResponseRabbitListener<T> extends CountingContextAwareRabbitListener<T>
{
    private RabbitTemplate responseRabbitTemplate;

    public RequestResponseRabbitListener(RabbitTemplate responseRabbitTemplate)
    {
        this.responseRabbitTemplate = responseRabbitTemplate;
    }

    @Override
    protected void handleEvent(T requestObject, MessageProperties messageProperties)
    {
        String correlationId = messageProperties.getCorrelationId();
        String replyQueueName = messageProperties.getReplyTo();

        Assert.notNull(correlationId, "correlation_id must not be null");
        Assert.notNull(replyQueueName, "reply_to must not be null");

        Object responseObject = processRequest(requestObject);

        if (responseObject != null)
        {
            responseRabbitTemplate.convertAndSend("", replyQueueName, responseObject, message ->
            {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }

    protected abstract Object processRequest(T requestObject);
}
