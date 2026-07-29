package com.avides.spring.rabbit.converter;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.avides.spring.rabbit.listener.CountingContextAwareRabbitListener;
import com.avides.spring.rabbit.listener.CountingRabbitListener;
import com.avides.spring.rabbit.listener.RabbitListener;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@link SpringRabbitMessageConverter} to simplify the JSON handling.
 * <p>
 * This message converter uses the generic type of the {@link RabbitListener} to convert the JSON to the Java Object
 * <p>
 * <b>Attention</b>: Unfortunately this only works if the {@link CountingRabbitListener} or {@link CountingContextAwareRabbitListener} is used!
 */
public class SpringRabbitJsonMessageConverter extends Jackson2JsonMessageConverter implements SpringRabbitMessageConverter
{
    /**
     * Construct with an internal {@link ObjectMapper} instance.
     * <p>
     * Trusted packages stay at the Spring AMQP default ({@code java.util}, {@code java.lang}). They only apply to the inherited
     * {@link #fromMessage(Message)}; the listener-driven {@link #fromMessage(Message, Class)} bypasses the type mapper.
     *
     * @since 1.6.11
     */
    public SpringRabbitJsonMessageConverter()
    {
        super();
    }

    /**
     * Construct with the provided {@link ObjectMapper} instance.
     * <p>
     * Trusted packages stay at the Spring AMQP default ({@code java.util}, {@code java.lang}). They only apply to the inherited
     * {@link #fromMessage(Message)}; the listener-driven {@link #fromMessage(Message, Class)} bypasses the type mapper.
     *
     * @param objectMapper the {@link ObjectMapper} to use.
     */
    public SpringRabbitJsonMessageConverter(ObjectMapper objectMapper)
    {
        super(objectMapper);
    }

    @Override
    public <T> T fromMessage(Message message, Class<T> clazz)
    {
        try
        {
            return objectMapper.readValue(message.getBody(), clazz);
        }
        catch (IOException e)
        {
            throw new MessageConversionException("Could not convert incoming message with class [" + clazz + "] and body [" + new String(message
                    .getBody()) + "]", e);
        }
    }
}
