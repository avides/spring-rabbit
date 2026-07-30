package com.avides.spring.rabbit.configuration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.avides.spring.rabbit.utils.DomainTestSupport;

class QueueMasterLocatorConnectionFactoryTest implements DomainTestSupport
{
    private QueueMasterLocatorConnectionFactory queueMasterLocatorConnectionFactory;

    private final ConnectionFactory defaultConnectionFactory = mock(ConnectionFactory.class);

    @BeforeEach
    void setUp()
    {
        queueMasterLocatorConnectionFactory = new QueueMasterLocatorConnectionFactory(defaultConnectionFactory, getCompleteRabbitProperties(), 15672);
    }

    @Test
    void testGetTargetConnectionFactoryWithMoreThanOneQueueException()
    {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> queueMasterLocatorConnectionFactory.getTargetConnectionFactory("testQueue, testQueue2"));

        assertEquals("Cannot use QueueMasterLocatorConnectionFactory with more than one queue: testQueue, testQueue2", e.getMessage());
    }

    @Test
    void testGetTargetConnectionFactoryWithConnectException()
    {
        when(defaultConnectionFactory.getHost()).thenReturn("localhost");
        when(defaultConnectionFactory.getUsername()).thenReturn("guest");

        ConnectionFactory resolved = queueMasterLocatorConnectionFactory.getTargetConnectionFactory("testQueue");

        verify(defaultConnectionFactory).getHost();
        verify(defaultConnectionFactory).getUsername();
        assertEquals(defaultConnectionFactory, resolved);
    }

    @Test
    void testResolveMasterNodeForQueue()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(getQueueInfo());

        assertThat(masterNode).isEqualTo("localhost");
    }

    @Test
    void testResolveMasterNodeForQueueWithoutQueueInfo()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(null);

        assertThat(masterNode).isNull();
    }

    @Test
    void testResolveMasterNodeForQueueWithExclusive()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(getQueueInfoWithExclusive());

        assertThat(masterNode).isNull();
    }
}
