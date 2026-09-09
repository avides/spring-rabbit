package com.avides.spring.rabbit.listener;

import java.util.function.Supplier;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.validation.annotation.Validated;

/**
 * {@link RabbitListener} with the option to access the {@link MessageProperties}
 *
 * @see RabbitListener
 * @param <T> expected type of the incoming object
 * @deprecated use {@link SpringRabbitListener}, will be deleted soon
 */
@Validated
@Deprecated(forRemoval = true)
public interface ContextAwareRabbitListener<T> extends SpringRabbitListener<T>
{
    /**
     * Helper method to simplify the tests
     * <p>
     * <code>@VisibleForTesting</code>
     *
     * @see #handle(Object, MessageProperties)
     * @param objectSupplier supplier to modify / create an inbound
     * @param messageProperties the message properties
     */
    default void handle(Supplier<T> objectSupplier, MessageProperties messageProperties)
    {
        handle(objectSupplier.get(), messageProperties);
    }

    /**
     * Helper method to simplify the tests
     * <p>
     * <code>@VisibleForTesting</code>
     *
     * @see #handle(Object, MessageProperties)
     * @param objectSupplier supplier to modify / create an inbound
     * @param messagePropertiesSupplier supplier to modify / create message properties
     */
    default void handle(Supplier<T> objectSupplier, Supplier<MessageProperties> messagePropertiesSupplier)
    {
        handle(objectSupplier.get(), messagePropertiesSupplier.get());
    }
}
