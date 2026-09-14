package com.avides.spring.rabbit.listener;

import java.lang.reflect.Type;

import jakarta.validation.Valid;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.ResolvableType;
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
     * Helper method to resolve the generic type, type-arguments and all.
     * <p>
     * Shall not be used by developers directly! Currently used for the {@link SpringRabbitMessageConverter}, which needs the full type to read a message whose
     * type is itself generic - a listener declared as {@code AbstractSpringRabbitListener<Box<Integer>>} has to be handed {@code Box<Integer>}, not a bare
     * {@code Box} whose content-type is gone.
     *
     * @return the generic type
     * @since 4.1.0
     */
    default Type getGenericType()
    {
        return ListenerTypeResolver.resolveMessageType(getClass(), SpringRabbitListener.class);
    }

    /**
     * Helper method to resolve the class of the generic type.
     * <p>
     * Shall not be used by developers directly! Where the generic type is itself generic, this only hands back its raw class and the content-type is gone,
     * which is why nothing reads a message through it any more.
     *
     * @return the class of the generic type
     * @deprecated superseded by {@link #getGenericType()}, which keeps the type-arguments
     */
    @Deprecated(since = "4.1.0")
    @SuppressWarnings("unchecked")
    default Class<T> getGenericTypeClass()
    {
        return (Class<T>) ResolvableType.forType(getGenericType()).toClass();
    }
}
