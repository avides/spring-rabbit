package com.avides.spring.rabbit.configuration;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.OtherTestClass;
import com.avides.spring.rabbit.test.support.OtherTestClassListener;
import com.avides.spring.rabbit.test.support.TestBox;
import com.avides.spring.rabbit.test.support.TestClass;
import com.avides.spring.rabbit.test.support.TestClassBoxListener;
import com.avides.spring.rabbit.test.support.TestClassListener;

@ActiveProfiles({ "it", "springRabbitJsonMessageConverter" })
class SpringRabbitAutoConfigurationForSpringRabbitJsonMessageConverterIT extends AbstractIT
{
    @Autowired
    private TestClassListener testClassListener;

    @Autowired
    private OtherTestClassListener otherTestClassListener;

    @Autowired
    private RabbitTemplate testClassRabbitTemplate;

    @Autowired
    private RabbitTemplate otherTestClassRabbitTemplate;

    @Autowired
    private RabbitTemplate receiveRabbitTemplate;

    @Autowired
    private TestClassBoxListener testClassBoxListener;

    @Autowired
    private RabbitTemplate testClassBoxRabbitTemplate;

    @AfterEach
    void clearInbounds()
    {
        testClassListener.getInbounds().clear();
        otherTestClassListener.getInbounds().clear();
        testClassBoxListener.getInbounds().clear();
    }

    @Test
    void testGetGenericTypeClass()
    {
        assertThat(testClassListener.getGenericTypeClass()).isEqualTo(TestClass.class);
        assertThat(otherTestClassListener.getGenericTypeClass()).isEqualTo(OtherTestClass.class);
    }

    /**
     * A listener whose message-type is itself generic - and which sits behind a listener-base of its own that binds only the content-type. Both used to be
     * impossible: the message-type was read off the direct superclass and cast to {@link Class}, so a parameterized one threw and its content-type was lost
     * either way.
     */
    @Test
    void testHandleWithAGenericMessageType()
    {
        assertThat(testClassBoxListener.getGenericType().getTypeName())
                .isEqualTo(TestBox.class.getTypeName() + "<" + TestClass.class.getTypeName() + ">");
        assertThat(testClassBoxListener.getGenericTypeClass()).isEqualTo(TestBox.class);

        testClassBoxRabbitTemplate.convertAndSend(new TestBox<>("the-label", TestClass.buildComplete()));

        await().until(() ->
        {
            if (!testClassBoxListener.getInbounds().isEmpty())
            {
                assertThat(testClassBoxListener.getInbounds()).singleElement().satisfies(box ->
                {
                    assertThat(box.getLabel()).isEqualTo("the-label");
                    // a TestClass, not the LinkedHashMap a raw TestBox would have left here
                    assertThat(box.getContent()).isEqualTo(TestClass.buildComplete());
                });
                return TRUE;
            }
            return FALSE;
        });
    }

    @Test
    void testHandleWithOnlyOneListenerGetMessages()
    {
        testClassRabbitTemplate.convertAndSend(TestClass.buildBase());
        testClassRabbitTemplate.convertAndSend(TestClass.buildComplete());

        await().until(() ->
        {
            if (2 == testClassListener.getInbounds().size())
            {
                assertThat(testClassListener.getInbounds()).hasSize(2);
                assertThat(testClassListener.getInbounds().get(0)).isEqualTo(TestClass.buildBase());
                assertThat(testClassListener.getInbounds().get(1)).isEqualTo(TestClass.buildComplete());
                return TRUE;
            }
            return FALSE;
        });
    }

    @Test
    void testHandleWithMultipleListenerGetMessages()
    {
        testClassRabbitTemplate.convertAndSend(TestClass.buildBase());
        testClassRabbitTemplate.convertAndSend(TestClass.buildComplete());

        otherTestClassRabbitTemplate.convertAndSend(OtherTestClass.buildComplete());

        await().until(() ->
        {
            if (2 == testClassListener.getInbounds().size() && !otherTestClassListener.getInbounds().isEmpty())
            {
                assertThat(testClassListener.getInbounds()).hasSize(2);
                assertThat(testClassListener.getInbounds().get(0)).isEqualTo(TestClass.buildBase());
                assertThat(testClassListener.getInbounds().get(1)).isEqualTo(TestClass.buildComplete());

                assertThat(otherTestClassListener.getInbounds()).hasSize(1).element(0).isEqualTo(OtherTestClass.buildComplete());
                return TRUE;
            }
            return FALSE;
        });
    }

    /**
     * Verifies that only the objectMapper tries to map the properties by the JSON-Properties
     */
    @Test
    void testHandleWithDifferentClassAsExpectedInbound()
    {
        testClassRabbitTemplate.convertAndSend(OtherTestClass.buildBase());

        await().until(() ->
        {
            if (!testClassListener.getInbounds().isEmpty())
            {
                assertThat(testClassListener.getInbounds()).hasSize(1).element(0).isEqualTo(new TestClass());
                return TRUE;
            }
            return FALSE;
        });
    }

    @Test
    void testHandleWithException()
    {
        testClassRabbitTemplate.convertAndSend("<test>");

        await().until(() ->
        {
            receiveRabbitTemplate.setDefaultReceiveQueue("com.avides.spring.rabbit.othertestclass.dlx");
            assertThat(receiveRabbitTemplate.receive()).isNull();

            receiveRabbitTemplate.setDefaultReceiveQueue("com.avides.spring.rabbit.testclass.dlx");
            assertThat(new String(receiveRabbitTemplate.receive().getBody())).isEqualTo("\"<test>\"");

            return testClassListener.getInbounds().isEmpty() ? TRUE : FALSE;
        });
    }

    @Test
    void testHandleWithUnknownTypeId()
    {
        testClassRabbitTemplate.convertAndSend(TestClass.buildBase(), message ->
        {
            message.getMessageProperties()
                    .getHeaders()
                    .put("__TypeId__", ExchangeProperties.class.getName());
            return message;
        });

        await().until(() ->
        {
            if (!testClassListener.getInbounds().isEmpty())
            {
                assertThat(testClassListener.getInbounds()).hasSize(1).element(0).isEqualTo(TestClass.buildBase());
                return TRUE;
            }
            return FALSE;
        });
    }
}
