package com.avides.spring.rabbit.converter;

import java.lang.reflect.Type;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.core.ResolvableType;

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

    /**
     * Convert from a Message to a Java object of the given type, keeping its type-arguments.
     * <p>
     * A converter that can make use of them - as {@link SpringRabbitJsonMessageConverter} does - overrides this; the default reads the message as the raw
     * class, which is what every converter did before this method existed.
     *
     * @param <T> the expected type
     * @param message the message to convert
     * @param type the expected type, possibly parameterized
     * @return instance of the expected type
     * @since 4.1.0
     */
    @SuppressWarnings("unchecked")
    default <T> T fromMessage(Message message, Type type)
    {
        return (T) fromMessage(message, ResolvableType.forType(type).toClass());
    }
}
