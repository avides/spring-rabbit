package com.avides.spring.rabbit.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

/**
 * Special message converter to simplify message conversion
 */
public interface SpringRabbitMessageConverter
{
    /**
     * Convert a Java object to a Message.
     *
     * @param object the object to convert
     * @param messageProperties The message properties.
     * @return the Message
     */
    Message toMessage(Object object, MessageProperties messageProperties);

    /**
     * Convert from a Message to a Java object.
     *
     * @param <T> the expected class
     * @param message the message to convert
     * @param clazz the class of the expected Java Object
     * @return instance of the expected class
     */
    <T> T fromMessage(Message message, Class<T> clazz);
}
