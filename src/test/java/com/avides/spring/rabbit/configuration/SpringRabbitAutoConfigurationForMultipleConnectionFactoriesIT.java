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

@ActiveProfiles({ "multipleConnectionFactories" })
@SpringBootTest(classes = { SpringRabbitAutoConfigurationForMultipleConnectionFactoriesIT.TestConfiguration.class, DummyListenerZero.class, DummyListenerOne.class })
public class SpringRabbitAutoConfigurationForMultipleConnectionFactoriesIT extends AbstractIT
{
    @Autowired
    private RabbitAdmin firstRabbitAdmin;

    @Autowired
    private List<ConnectionFactory> connectionFactories;

    @Autowired
    private List<Queue> queues;

    @Autowired
    private List<DefaultMessageListenerContainer<Object>> listenerContainer;

    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.zero@firstConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerZeroContainer;

    @Qualifier("dummyListenerOne@com.avides.spring.rabbit.queue.one@secondConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerOneContainer;

    private Queue queueZero;

    private Queue queueZeroDlx;

    private Queue queueOne;

    private Queue queueOneDlx;

    @Test
    public void test()
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

    private void checkListenerContainer()
    {
        assertEquals(2, listenerContainer.size());

        assertEquals(AcknowledgeMode.AUTO, dummyListenerZeroContainer.getAcknowledgeMode());
        assertEquals(1, dummyListenerZeroContainer.getQueueNames().length);
        assertEquals("com.avides.spring.rabbit.queue.zero", dummyListenerZeroContainer.getQueueNames()[0]);
        assertEquals("guest", dummyListenerZeroContainer.getConnectionFactory().getUsername());
        assertEquals("/", dummyListenerZeroContainer.getConnectionFactory().getVirtualHost());
        assertEquals(host, dummyListenerZeroContainer.getConnectionFactory().getHost());
        assertTrue(dummyListenerZeroContainer.isActive());
        assertEquals(500, getPrefetchCount(dummyListenerZeroContainer));

        assertEquals(AcknowledgeMode.AUTO, dummyListenerOneContainer.getAcknowledgeMode());
        assertEquals(1, dummyListenerOneContainer.getQueueNames().length);
        assertEquals("com.avides.spring.rabbit.queue.one", dummyListenerOneContainer.getQueueNames()[0]);
        assertEquals("guest", dummyListenerOneContainer.getConnectionFactory().getUsername());
        assertEquals("/", dummyListenerOneContainer.getConnectionFactory().getVirtualHost());
        assertEquals(host, dummyListenerOneContainer.getConnectionFactory().getHost());
        assertTrue(dummyListenerOneContainer.isActive());
        assertEquals(500, getPrefetchCount(dummyListenerOneContainer));
    }

    private void checkQueues()
    {
        // check if queues really exist
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueZero.getName()));
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueZeroDlx.getName()));
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueOne.getName()));
        assertNotNull(firstRabbitAdmin.getQueueProperties(queueOneDlx.getName()));

        assertEquals(4, queues.size());

        // queueZero
        assertEquals("com.avides.spring.rabbit.queue.zero", queueZero.getName());
        assertEquals(1, queueZero.getDeclaringAdmins().size());
        assertEquals(firstRabbitAdmin, queueZero.getDeclaringAdmins().iterator().next());
        assertEquals(3, queueZero.getArguments().size());
        assertTrue(String.valueOf(queueZero.getArguments().get("x-dead-letter-exchange")).isEmpty());
        assertEquals("com.avides.spring.rabbit.queue.zero.dlx", queueZero.getArguments().get("x-dead-letter-routing-key"));
        assertEquals(Long.valueOf(50), queueZero.getArguments().get("x-max-length"));

        // queueZeroDlx
        assertEquals("com.avides.spring.rabbit.queue.zero.dlx", queueZeroDlx.getName());
        assertEquals(1, queueZeroDlx.getDeclaringAdmins().size());
        assertEquals(firstRabbitAdmin, queueZeroDlx.getDeclaringAdmins().iterator().next());
        assertEquals(2, queueZeroDlx.getArguments().size());
        assertEquals(Long.valueOf(50), queueZeroDlx.getArguments().get("x-max-length"));
        assertEquals("lazy", queueZeroDlx.getArguments().get("x-queue-mode"));

        // queueOne
        assertEquals("com.avides.spring.rabbit.queue.one", queueOne.getName());
        assertEquals(1, queueOne.getDeclaringAdmins().size());
        // assertEquals(rabbitAdmin, queueOne.getDeclaringAdmins().iterator().next()); Currently the queue is declared by a different rabbitAdmin and the
        // application not saves it
        assertEquals(5, queueOne.getArguments().size());
        assertTrue(String.valueOf(queueOne.getArguments().get("x-dead-letter-exchange")).isEmpty());
        assertEquals("com.avides.spring.rabbit.queue.one.dlx", queueOne.getArguments().get("x-dead-letter-routing-key"));
        assertEquals(Long.valueOf(1), queueOne.getArguments().get("x-max-length"));
        assertEquals("lazy", queueOne.getArguments().get("x-queue-mode"));
        assertEquals("test", queueOne.getArguments().get("someAdditionalQueueArgumentsKey"));

        // queueOneDlx
        assertEquals("com.avides.spring.rabbit.queue.one.dlx", queueOneDlx.getName());
        assertEquals(1, queueOneDlx.getDeclaringAdmins().size());
        // assertEquals(rabbitAdmin, queueOneDlx.getDeclaringAdmins().iterator().next()); Currently the queue is declared by a different rabbitAdmin and the
        // application not saves it
        assertEquals(2, queueOneDlx.getArguments().size());
        assertEquals(Long.valueOf(1), queueOneDlx.getArguments().get("x-max-length"));
        assertEquals("lazy", queueOneDlx.getArguments().get("x-queue-mode"));
    }

    @EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
    @Configuration
    static class TestConfiguration extends AbstractIT.TestConfiguration
    {
        // nothing, just to exclude RabbitAutoConfiguration
    }
}
