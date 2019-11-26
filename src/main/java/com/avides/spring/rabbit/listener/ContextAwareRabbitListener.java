package com.avides.spring.rabbit.listener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import javax.validation.Valid;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.validation.annotation.Validated;

import com.avides.spring.rabbit.converter.SpringRabbitMessageConverter;

/**
 * {@link RabbitListener} with the option to access the {@link MessageProperties}
 *
 * @see RabbitListener
 * @param <T> expected type of the incoming object
 */
@Validated
public interface ContextAwareRabbitListener<T>
{
    /**
     * Called by an incoming message after the message got unmarshaled by a {@link MessageConverter}
     *
     * @param object the incoming object
     * @param messageProperties the message properties of the message
     */
    void handle(@Valid T object, MessageProperties messageProperties);

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

    /**
     * Helper method to resolve the class of the generic type
     * <p>
     * Currently used for the {@link SpringRabbitMessageConverter}
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
