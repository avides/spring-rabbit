package com.avides.spring.rabbit.converter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.core.ResolvableType;

import com.avides.spring.rabbit.test.support.TestClass;
import com.fasterxml.jackson.databind.ObjectMapper;

class SpringRabbitJsonMessageConverterTest
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
    void testToMessageWithDefaultObjectMapper()
    {
        Message message = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualTo(MESSAGE);
    }

    @Test
    void testToMessageWithCustomMessageConverter()
    {
        Message message = MESSAGE_CONVERTER_CUSTOM_OBJECT_MAPPER.toMessage(TestClass.buildBase(), new MessageProperties());

        assertThat(message).isEqualTo(MESSAGE_WITH_CUSTOM);
    }

    @Test
    void testFromMessageWithDefaultObjectMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE, TestClass.class);

        assertThat(testClass).isEqualTo(TestClass.buildBase());
    }

    @Test
    void testFromMessageWithCustomObjectMapper()
    {
        TestClass testClass = MESSAGE_CONVERTER_CUSTOM_OBJECT_MAPPER.fromMessage(MESSAGE_WITH_CUSTOM, TestClass.class);

        assertThat(testClass).isEqualTo(TestClass.buildBase());
    }

    @Test
    void testFromMessageWithError()
    {
        assertThatThrownBy(() -> MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE_WITH_CUSTOM, String.class))
                .hasMessage("Could not convert incoming message with class [class java.lang.String] and body [{\"floatProperty\":2.0}]");
    }

    @Nested
    class FromMessageByTypeTests
    {
        private final Message listMessage = new Message("[{\"floatProperty\":2.0}]".getBytes(), MessagePropertiesBuilder.newInstance()
                .setContentType("application/json")
                .setContentEncoding("UTF-8")
                .build());

        /**
         * The point of reading by type instead of by class: without the type-argument, Jackson can only hand back a list of {@code LinkedHashMap}s.
         */
        @Test
        void testFromMessageKeepsTheTypeArguments()
        {
            var type = ResolvableType.forClassWithGenerics(List.class, TestClass.class).getType();

            List<TestClass> testClasses = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(listMessage, type);

            assertThat(testClasses).singleElement().isInstanceOf(TestClass.class).satisfies(testClass -> assertThat(testClass.getFloatProperty()).isEqualTo(2f));
        }

        @Test
        void testFromMessageWithAPlainClassAsType()
        {
            TestClass testClass = MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE, (Type) TestClass.class);

            assertThat(testClass).isEqualTo(TestClass.buildBase());
        }

        @Test
        void testFromMessageByTypeWithError()
        {
            assertThatThrownBy(() -> MESSAGE_CONVERTER_DEFAULT_MESSAGE_CONVERTER.fromMessage(MESSAGE_WITH_CUSTOM, (Type) String.class))
                    .hasMessage("Could not convert incoming message with type [class java.lang.String] and body [{\"floatProperty\":2.0}]");
        }

        /**
         * A converter that does not implement the type-variant keeps working - it reads the raw class, exactly as every converter did before.
         */
        @Test
        void testTheDefaultFallsBackToTheRawClass()
        {
            var converter = new SpringRabbitMessageConverter()
            {
                @Override
                public Message toMessage(Object object, MessageProperties messageProperties)
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T> T fromMessage(Message message, Class<T> clazz)
                {
                    return (T) clazz;
                }
            };

            assertThat((Object) converter.fromMessage(listMessage, ResolvableType.forClassWithGenerics(List.class, TestClass.class).getType()))
                    .isEqualTo(List.class);
        }
    }
}
