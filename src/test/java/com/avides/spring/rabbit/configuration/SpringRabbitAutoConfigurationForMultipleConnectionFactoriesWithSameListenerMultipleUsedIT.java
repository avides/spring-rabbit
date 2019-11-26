package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.DummyListenerOne;
import com.avides.spring.rabbit.test.support.DummyListenerZero;

@ActiveProfiles({ "multipleConnectionFactoriesWithSameListenerMultipleUsed" })
@SpringBootTest(classes = { SpringRabbitAutoConfigurationForMultipleConnectionFactoriesWithSameListenerMultipleUsedIT.TestConfiguration.class, DummyListenerZero.class, DummyListenerOne.class })
public class SpringRabbitAutoConfigurationForMultipleConnectionFactoriesWithSameListenerMultipleUsedIT extends AbstractIT
{
    @Autowired
    private RabbitAdmin firstRabbitAdmin;

    @Autowired
    private RabbitAdmin secondRabbitAdmin;

    @Autowired
    private List<ConnectionFactory> connectionFactories;

    @Autowired
    private List<Queue> queues;

    @Autowired
    private List<DefaultMessageListenerContainer<Object>> listenerContainer;

    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.zero@firstConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> firstDummyListenerContainer;

    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.one@secondConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> secondDummyListenerContainer;

    private Queue queueZero;

    private Queue queueZeroDlx;

    private Queue queueOne;

    private Queue queueOneDlx;

    @Test
    public void testAutoRabbitConfigurationForMultipleConnectionFactoriesWithSameListenerMultipleUsed()
    {
        initializeQueueVariables();

        checkConnectionFactories();
        checkQueues();
        checkListenerContainer();
    }

    // beanName of queues = queueName +"_"+ host -> host is different-> could not be autowired with @Autowired
    private void initializeQueueVariables()
    {
        queueZero = applicationContext.getBean("com.avides.spring.rabbit.queue.zero_" + host, Queue.class);
        queueZeroDlx = applicationContext.getBean("com.avides.spring.rabbit.queue.zero.dlx_" + host, Queue.class);
        queueOne = applicationContext.getBean("com.avides.spring.rabbit.queue.one_" + host, Queue.class);
        queueOneDlx = applicationContext.getBean("com.avides.spring.rabbit.queue.one.dlx_" + host, Queue.class);
    }

    private void checkConnectionFactories()
    {
        assertEquals(2, connectionFactories.size());
        connectionFactories.forEach(cf -> assertThat(cf).isInstanceOf(ConnectionFactory.class));
    }

    private void checkQueues()
    {
        // check if queues really exist
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueZero.getName()));
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueZeroDlx.getName()));
        assertNotNull(secondRabbitAdmin.getQueueProperties(queueOne.getName()));
        assertNotNull(secondRabbitAdmin.getQueueProperties(queueOneDlx.getName()));

        assertEquals(4, queues.size());

        assertEquals(Integer.valueOf(1), secondRabbitAdmin.getQueueProperties(queueZero.getName()).get(RabbitAdmin.QUEUE_CONSUMER_COUNT));
        assertEquals(Integer.valueOf(0), secondRabbitAdmin.getQueueProperties(queueZeroDlx.getName()).get(RabbitAdmin.QUEUE_CONSUMER_COUNT));

        assertEquals(Integer.valueOf(1), secondRabbitAdmin.getQueueProperties(queueOne.getName()).get(RabbitAdmin.QUEUE_CONSUMER_COUNT));
        assertEquals(Integer.valueOf(0), secondRabbitAdmin.getQueueProperties(queueOneDlx.getName()).get(RabbitAdmin.QUEUE_CONSUMER_COUNT));
    }

    private void checkListenerContainer()
    {
        assertEquals(2, listenerContainer.size());

        assertEquals(AcknowledgeMode.AUTO, firstDummyListenerContainer.getAcknowledgeMode());
        assertEquals(1, firstDummyListenerContainer.getQueueNames().length);
        assertEquals("com.avides.spring.rabbit.queue.zero", firstDummyListenerContainer.getQueueNames()[0]);
        assertEquals("guest", firstDummyListenerContainer.getConnectionFactory().getUsername());
        assertEquals("/", firstDummyListenerContainer.getConnectionFactory().getVirtualHost());
        assertEquals(host, firstDummyListenerContainer.getConnectionFactory().getHost());
        assertTrue(firstDummyListenerContainer.isActive());

        assertEquals(AcknowledgeMode.AUTO, secondDummyListenerContainer.getAcknowledgeMode());
        assertEquals(1, secondDummyListenerContainer.getQueueNames().length);
        assertEquals("com.avides.spring.rabbit.queue.one", secondDummyListenerContainer.getQueueNames()[0]);
        assertEquals("guest", secondDummyListenerContainer.getConnectionFactory().getUsername());
        assertEquals("/", secondDummyListenerContainer.getConnectionFactory().getVirtualHost());
        assertEquals(host, secondDummyListenerContainer.getConnectionFactory().getHost());
        assertTrue(secondDummyListenerContainer.isActive());
    }

    @EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
    @Configuration
    static class TestConfiguration extends AbstractIT.TestConfiguration
    {
        // nothing, just to exclude RabbitAutoConfiguration
    }
}
