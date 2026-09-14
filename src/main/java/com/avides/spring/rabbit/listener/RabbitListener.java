package com.avides.spring.rabbit.listener;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import jakarta.validation.Valid;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.ResolvableType;
import org.springframework.validation.annotation.Validated;

import com.avides.spring.rabbit.converter.SpringRabbitMessageConverter;

/**
 * Listener that provides the handling of incoming messages
 * <p>
 * After the message got unmarshaled, the object will be validated
 *
 * @param <T> expected type of the incoming object
 * @deprecated use {@link SpringRabbitListener}, will be deleted soon
 */
@Validated
@Deprecated(forRemoval = true)
public interface RabbitListener<T>
{
    /**
     * Called by an incoming message after the message got unmarshaled by a {@link MessageConverter}
     *
     * @param object the incoming object
     */
    void handle(@Valid T object);

    /**
     * Helper method to simplify the tests
     * <p>
     * <code>@VisibleForTesting</code>
     *
     * @see #handle(Object)
     * @param supplier supplier to modify / create an inbound
     */
    default void handle(Supplier<T> supplier)
    {
        handle(supplier.get());
    }

    /**
     * Helper method to resolve the generic type, type-arguments and all
     * <p>
     * Currently used for the {@link SpringRabbitMessageConverter}
     *
     * @return the generic type
     * @since 4.1.0
     */
    default Type getGenericType()
    {
        return ListenerTypeResolver.resolveMessageType(getClass(), RabbitListener.class);
    }

    /**
     * Helper method to resolve the class of the generic type
     * <p>
     * Where the generic type is itself generic, this only hands back its raw class and the content-type is gone, which is why nothing reads a message through
     * it any more.
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
