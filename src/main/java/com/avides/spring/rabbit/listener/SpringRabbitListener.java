package com.avides.spring.rabbit.listener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.validation.Valid;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.validation.annotation.Validated;

import com.avides.spring.rabbit.converter.SpringRabbitMessageConverter;

/**
 * Handles unmarshaled RabbitMQ messages with the option to access the {@link MessageProperties}. Unmarshaled messages are validated.
 *
 * @param <T> expected type of the incoming object
 */
@Validated
public interface SpringRabbitListener<T>
{
    /**
     * Called by an incoming message after the message got unmarshaled by a {@link MessageConverter}.
     *
     * @param object the incoming object
     * @param messageProperties the message properties of the message
     */
    void handle(@Valid T object, MessageProperties messageProperties);

    /**
     * Helper method to resolve the class of the generic type.
     * <p>
     * Shall not be used by developer directly! Currently used for the {@link SpringRabbitMessageConverter}.
     *
     * @return the class of the generic type
     */
    @SuppressWarnings("unchecked")
    default Class<T> getGenericTypeClass()
    {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }
}
