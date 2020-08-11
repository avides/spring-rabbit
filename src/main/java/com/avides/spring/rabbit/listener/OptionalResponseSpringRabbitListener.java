package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Abstract implementation of {@link SpringRabbitListener} with metrics to handle request messages and eventually produce corresponding response messages.
 *
 * @param <T> expected type of the incoming object
 */
public abstract class OptionalResponseSpringRabbitListener<T> extends AbstractSpringRabbitListener<T>
{
    private RabbitTemplate responseRabbitTemplate;

    /**
     * Constructs a new {@link OptionalResponseSpringRabbitListener} with the given {@link RabbitTemplate} for responses.
     *
     * @param responseRabbitTemplate used for response messages
     */
    public OptionalResponseSpringRabbitListener(RabbitTemplate responseRabbitTemplate)
    {
        this.responseRabbitTemplate = responseRabbitTemplate;
    }

    /**
     * Processes unmarshaled request message and returns the (not yet marshalled) response message.
     *
     * @param requestObject unmarshalled request message
     * @return response message (not yet marshalled)
     */
    protected abstract Object processRequest(T requestObject);

    /**
     * Handles given unmarshalled message with its properties and eventually (if response object and queue are given) sends a response message. Called by
     * {@link #handle(Object, MessageProperties)} which also collects some metrics. Calls {@link #processRequest(Object)} by default which must be overridden.
     *
     * @param requestObject the unmarshaled object
     * @param messageProperties the message properties
     */
    @Override
    protected void handleEvent(T requestObject, MessageProperties messageProperties)
    {
        var replyQueueName = messageProperties.getReplyTo();

        var responseObject = processRequest(requestObject);

        if (responseObject != null && replyQueueName != null)
        {
            responseRabbitTemplate.convertAndSend("", replyQueueName, responseObject, message ->
            {
                message.getMessageProperties().setCorrelationId(messageProperties.getCorrelationId());
                return message;
            });
        }
    }
}
