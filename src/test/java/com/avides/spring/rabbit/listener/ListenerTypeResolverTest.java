package com.avides.spring.rabbit.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;

import com.avides.spring.rabbit.test.support.TestClass;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The message-type has to be resolved through the whole class-hierarchy and with its type-arguments intact - the two things the previous implementation, which
 * only read the first type-argument of the direct superclass, got wrong.
 */
class ListenerTypeResolverTest
{
    static class Box<E>
    {
        // nothing to do here
    }

    @Nested
    class SpringRabbitListenerTests
    {
        /**
         * The plain case: the listener extends the base directly.
         */
        @Test
        void testMessageTypeOfADirectListener()
        {
            var listener = new AbstractSpringRabbitListener<TestClass>()
            {
                @Override
                protected void handleEvent(TestClass object, MessageProperties messageProperties)
                {
                    // nothing to do here
                }
            };

            assertThat(listener.getGenericType()).isEqualTo(TestClass.class);
            assertThat(listener.getGenericTypeClass()).isEqualTo(TestClass.class);
        }

        /**
         * A listener-base of its own between the listener and {@link AbstractSpringRabbitListener}, carrying type-parameters of its own - reading the first
         * type-argument of the direct superclass would hand back {@code String} here.
         */
        @Test
        void testMessageTypeThroughAListenerBaseWithOwnTypeParameters()
        {
            assertThat(new ListenerWithOwnBase().getGenericType()).isEqualTo(TestClass.class);
            assertThat(new ListenerWithOwnBase().getGenericTypeClass()).isEqualTo(TestClass.class);
        }

        /**
         * A listener two classes away from the base - the direct superclass has no type-arguments at all, which used to throw.
         */
        @Test
        void testMessageTypeOfAListenerTwoLevelsDown()
        {
            assertThat(new ListenerTwoLevelsDown().getGenericType()).isEqualTo(TestClass.class);
            assertThat(new ListenerTwoLevelsDown().getGenericTypeClass()).isEqualTo(TestClass.class);
        }

        /**
         * A message-type that is itself generic: the type-arguments have to survive, or a converter can only read the raw class.
         */
        @Test
        void testMessageTypeKeepsItsTypeArguments()
        {
            var listener = new ListenerWithGenericMessage();

            assertThat(listener.getGenericType().getTypeName())
                    .isEqualTo(Box.class.getTypeName() + "<" + TestClass.class.getTypeName() + ">");
            assertThat(listener.getGenericTypeClass()).isEqualTo(Box.class);
        }

        @Test
        void testMessageTypeThatCannotBeResolved()
        {
            assertThatThrownBy(new RawListener()::getGenericType)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("could not resolve the message-type");
        }
    }

    @Nested
    class RabbitListenerTests
    {
        @Test
        void testMessageTypeOfADeprecatedListener()
        {
            var listener = new CountingRabbitListener<TestClass>()
            {
                @Override
                protected void handleEvent(TestClass object)
                {
                    // nothing to do here
                }
            };

            assertThat(listener.getGenericType()).isEqualTo(TestClass.class);
            assertThat(listener.getGenericTypeClass()).isEqualTo(TestClass.class);
        }

        @Test
        void testMessageTypeOfADeprecatedContextAwareListener()
        {
            var listener = new CountingContextAwareRabbitListener<TestClass>()
            {
                @Override
                protected void handleEvent(TestClass object, MessageProperties messageProperties)
                {
                    // nothing to do here
                }
            };

            assertThat(listener.getGenericType()).isEqualTo(TestClass.class);
            assertThat(listener.getGenericTypeClass()).isEqualTo(TestClass.class);
        }
    }

    abstract static class ListenerBaseWithOwnTypeParameters<M, C> extends AbstractSpringRabbitListener<M>
    {
        // nothing to do here
    }

    static class ListenerWithOwnBase extends ListenerBaseWithOwnTypeParameters<TestClass, String>
    {
        @Override
        protected void handleEvent(TestClass object, MessageProperties messageProperties)
        {
            // nothing to do here
        }
    }

    static class ListenerTwoLevelsDown extends ListenerWithOwnBase
    {
        // nothing to do here
    }

    abstract static class BoxListenerBase<E> extends AbstractSpringRabbitListener<Box<E>>
    {
        // nothing to do here
    }

    static class ListenerWithGenericMessage extends BoxListenerBase<TestClass>
    {
        @Override
        protected void handleEvent(Box<TestClass> object, MessageProperties messageProperties)
        {
            // nothing to do here
        }
    }

    @SuppressWarnings("rawtypes")
    static class RawListener extends AbstractSpringRabbitListener
    {
        @Override
        protected void handleEvent(Object object, MessageProperties messageProperties)
        {
            // nothing to do here
        }
    }

    /**
     * Guards the assumption the resolver rests on: a message-type with its type-arguments intact is one Jackson can actually build a {@code JavaType} from.
     */
    @Test
    void testTheResolvedTypeIsUsableAsAJacksonType()
    {
        var javaType = new ObjectMapper().getTypeFactory().constructType(new ListenerWithGenericMessage().getGenericType());

        assertThat(javaType.getRawClass()).isEqualTo(Box.class);
        assertThat(javaType.containedType(0).getRawClass()).isEqualTo(TestClass.class);
    }
}
