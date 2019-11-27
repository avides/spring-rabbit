package com.avides.spring.rabbit.configuration;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.After;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.OtherTestClass;
import com.avides.spring.rabbit.test.support.OtherTestClassListener;
import com.avides.spring.rabbit.test.support.TestClass;
import com.avides.spring.rabbit.test.support.TestClassListener;

@ActiveProfiles({ "it", "springRabbitJsonMessageConverter" })
public class SpringRabbitAutoConfigurationForSpringRabbitJsonMessageConverterIT extends AbstractIT
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

    @After
    public void clearInbounds()
    {
        testClassListener.getInbounds().clear();
        otherTestClassListener.getInbounds().clear();
    }

    @Test
    public void testGetGenericTypeClass()
    {
        assertThat(testClassListener.getGenericTypeClass()).isEqualTo(TestClass.class);
        assertThat(otherTestClassListener.getGenericTypeClass()).isEqualTo(OtherTestClass.class);
    }

    @Test
    public void testHandleWithOnlyOneListenerGetMessages()
    {
        testClassRabbitTemplate.convertAndSend(TestClass.buildBase());
        testClassRabbitTemplate.convertAndSend(TestClass.buildComplete());

        await().until(() ->
        {
            if (2 == testClassListener.getInbounds().size())
            {
                assertThat(testClassListener.getInbounds()).hasSize(2);
                assertThat(testClassListener.getInbounds().get(0)).isEqualToComparingFieldByFieldRecursively(TestClass.buildBase());
                assertThat(testClassListener.getInbounds().get(1)).isEqualToComparingFieldByFieldRecursively(TestClass.buildComplete());
                return TRUE;
            }
            return FALSE;
        });
    }

    @Test
    public void testHandleWithMultipleListenerGetMessages()
    {
        testClassRabbitTemplate.convertAndSend(TestClass.buildBase());
        testClassRabbitTemplate.convertAndSend(TestClass.buildComplete());

        otherTestClassRabbitTemplate.convertAndSend(OtherTestClass.buildComplete());

        await().until(() ->
        {
            if (2 == testClassListener.getInbounds().size() && !otherTestClassListener.getInbounds().isEmpty())
            {
                assertThat(testClassListener.getInbounds()).hasSize(2);
                assertThat(testClassListener.getInbounds().get(0)).isEqualToComparingFieldByFieldRecursively(TestClass.buildBase());
                assertThat(testClassListener.getInbounds().get(1)).isEqualToComparingFieldByFieldRecursively(TestClass.buildComplete());

                assertThat(otherTestClassListener.getInbounds()).hasSize(1)
                        .element(0)
                        .isEqualToComparingFieldByFieldRecursively(OtherTestClass.buildComplete());
                return TRUE;
            }
            return FALSE;
        });
    }

    /**
     * Verifies that only the objectMapper tries to map the properties by the JSON-Properties
     */
    @Test
    public void testHandleWithDifferentClassAsExpectedInbound()
    {
        testClassRabbitTemplate.convertAndSend(OtherTestClass.buildBase());

        await().until(() ->
        {
            if (!testClassListener.getInbounds().isEmpty())
            {
                assertThat(testClassListener.getInbounds()).hasSize(1)
                        .element(0)
                        .isEqualToComparingFieldByFieldRecursively(new TestClass());
                return TRUE;
            }
            return FALSE;
        });
    }

    @Test
    public void testHandleWithException()
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
    public void testHandleWithUnknownTypeId()
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
                assertThat(testClassListener.getInbounds()).hasSize(1).element(0).isEqualToComparingFieldByFieldRecursively(TestClass.buildBase());
                return TRUE;
            }
            return FALSE;
        });
    }
}
