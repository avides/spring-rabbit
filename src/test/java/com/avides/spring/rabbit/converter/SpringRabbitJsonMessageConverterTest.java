package com.avides.spring.rabbit.converter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import com.avides.spring.rabbit.test.support.TestClass;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SpringRabbitJsonMessageConverterTest
{
    private static final Message MESSAGE_WITH_CUSTOM = new Message("{\"floatProperty\":2.0}".getBytes(), MessagePropertiesBuilder.newInstance()
            .setHeader("__TypeId__", TestClass.class.getName())
            .setContentType("application/json")
            .setContentEncoding("UTF-8")
            .setContentLength(21)
            .build());

    private static final SpringRabbitMessageConverter MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER = new SpringRabbitJsonMessageConverter();

    private static final Message MESSAGE = new Message("{\"floatProperty\":2.0,\"stringProperty\":null,\"integerProperty\":null,\"subTestClass\":null}"
            .getBytes(), MessagePropertiesBuilder.newInstance()
                    .setHeader("__TypeId__", TestClass.class.getName())
                    .setContentType("application/json")
                    .setContentEncoding("UTF-8")
                    .setContentLength(86)
                    .build());

    private static final ObjectMapper CUSTOM_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(NON_NULL);

    private static final SpringRabbitMessageConverter MESSAGE_CONVERTER_CUSTOM_OBJECT_MAPPER = new SpringRabbitJsonMessageConverter(CUSTOM_OBJECT_MAPPER);

    @Test
    public void testToMessageWithDefaultObjectMapper()
    {
        Message message = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualToComparingFieldByFieldRecursively(MESSAGE);
    }

    @Test
    public void testToMessageWithCustomMessageConverter()
    {
        Message message = MESSAGE_CONVERTER_CUSTOM_OBJECT_MAPPER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualToComparingFieldByFieldRecursively(MESSAGE_WITH_CUSTOM);
    }

    @Test
    public void testFromMessageWithDefaultObjectMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE, TestClass.class);

        assertThat(testClass).isEqualToComparingFieldByFieldRecursively(TestClass.buildBase());
    }

    @Test
    public void testFromMessageWithCustomObjectMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_CUSTOM_OBJECT_MAPPER.fromMessage(MESSAGE_WITH_CUSTOM, TestClass.class);

        assertThat(testClass).isEqualToComparingFieldByFieldRecursively(TestClass.buildBase());
    }

    @Test
    public void testFromMessageWithError()
    {
        assertThatThrownBy(() -> MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE_WITH_CUSTOM, String.class))
                .hasMessage("Could not convert incoming message with class [class java.lang.String] and body [{\"floatProperty\":2.0}]");
    }
}
