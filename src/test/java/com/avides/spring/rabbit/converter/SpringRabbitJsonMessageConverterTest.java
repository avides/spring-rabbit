package com.avides.spring.rabbit.converter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import com.avides.spring.rabbit.test.support.TestClass;

import tools.jackson.databind.json.JsonMapper;

class SpringRabbitJsonMessageConverterTest
{
    private static final Message MESSAGE_WITH_CUSTOM = new Message("{\"floatProperty\":2.0}".getBytes(), MessagePropertiesBuilder.newInstance()
            .setHeader("__TypeId__", TestClass.class.getName())
            .setContentType("application/json")
            .setContentEncoding("UTF-8")
            .setContentLength(21)
            .build());

    private static final SpringRabbitMessageConverter MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER = new SpringRabbitJsonMessageConverter();

    private static final Message MESSAGE = new Message("{\"floatProperty\":2.0,\"integerProperty\":null,\"stringProperty\":null,\"subTestClass\":null}"
            .getBytes(), MessagePropertiesBuilder.newInstance()
                    .setHeader("__TypeId__", TestClass.class.getName())
                    .setContentType("application/json")
                    .setContentEncoding("UTF-8")
                    .setContentLength(86)
                    .build());

    private static final JsonMapper CUSTOM_JSON_MAPPER = JsonMapper.builder()
            .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(NON_NULL))
            .build();

    private static final SpringRabbitMessageConverter MESSAGE_CONVERTER_CUSTOM_JSON_MAPPER = new SpringRabbitJsonMessageConverter(CUSTOM_JSON_MAPPER);

    @Test
    void testToMessageWithDefaultJsonMapper()
    {
        Message message = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualTo(MESSAGE);
    }

    @Test
    void testToMessageWithCustomMessageConverter()
    {
        Message message = MESSAGE_CONVERTER_CUSTOM_JSON_MAPPER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualTo(MESSAGE_WITH_CUSTOM);
    }

    @Test
    void testFromMessageWithDefaultJsonMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE, TestClass.class);

        assertThat(testClass).isEqualTo(TestClass.buildBase());
    }

    @Test
    void testFromMessageWithCustomJsonMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_CUSTOM_JSON_MAPPER.fromMessage(MESSAGE_WITH_CUSTOM, TestClass.class);

        assertThat(testClass).isEqualTo(TestClass.buildBase());
    }

    @Test
    void testFromMessageWithError()
    {
        assertThatThrownBy(() -> MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE_WITH_CUSTOM, String.class))
                .hasMessage("Could not convert incoming message with class [class java.lang.String] and body [{\"floatProperty\":2.0}]");
    }
}
