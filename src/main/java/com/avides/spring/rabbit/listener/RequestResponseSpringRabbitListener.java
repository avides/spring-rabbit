package com.avides.spring.rabbit.listener;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;

/**
 * Abstract implementation of {@link SpringRabbitListener} with metrics to handle request messages and produce corresponding response messages.
 *
 * @param <T> expected type of the incoming object
 */
public abstract class RequestResponseSpringRabbitListener<T> extends AbstractSpringRabbitListener<T>
{
    private RabbitTemplate responseRabbitTemplate;

    /**
     * Constructs a new {@link RequestResponseSpringRabbitListener} with the given {@link RabbitTemplate} for responses.
     *
     * @param responseRabbitTemplate used for response messages
     */
    public RequestResponseSpringRabbitListener(RabbitTemplate responseRabbitTemplate)
    {
        this.responseRabbitTemplate = responseRabbitTemplate;
    }

    /**
     * Processes unmarshaled request message and returns the (not yet marshaled) response message.
     *
     * @param requestObject unmarshaled request message
     * @return response message (not yet marshaled)
     */
    protected abstract Object processRequest(T requestObject);

    /**
     * Handles given unmarshaled message with its properties and sends a response message. Called by {@link #handle(Object, MessageProperties)} which also
     * collects some metrics. Calls {@link #processRequest(Object)} by default which must be overridden.
     *
     * You have to set "reply_to" in your {@link MessageProperties}. Additionally you can optional set a "correlation_id". These parameters are used for the
     * response message for better identification on request side.
     *
     * @param requestObject the unmarshaled object
     * @param messageProperties the message properties
     */
    @Override
    protected void handleEvent(T requestObject, MessageProperties messageProperties)
    {
        var correlationId = messageProperties.getCorrelationId();
        var replyQueueName = messageProperties.getReplyTo();

        Assert.notNull(replyQueueName, "reply_to must not be null");

        var responseObject = processRequest(requestObject);

        if (responseObject != null)
        {
            responseRabbitTemplate.convertAndSend("", replyQueueName, responseObject, message ->
            {
                message.getMessageProperties().setCorrelationId(correlationId);
                return message;
            });
        }
    }
}
