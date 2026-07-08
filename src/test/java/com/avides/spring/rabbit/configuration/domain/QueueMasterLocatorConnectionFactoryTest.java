package com.avides.spring.rabbit.configuration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.avides.spring.rabbit.utils.DomainTestSupport;

public class QueueMasterLocatorConnectionFactoryTest implements DomainTestSupport
{
    private QueueMasterLocatorConnectionFactory queueMasterLocatorConnectionFactory;

    private final ConnectionFactory defaultConnectionFactory = mock(ConnectionFactory.class);

    @BeforeEach
    public void setUp()
    {
        queueMasterLocatorConnectionFactory = new QueueMasterLocatorConnectionFactory(defaultConnectionFactory, getCompleteRabbitProperties(), 15672);
    }

    @Test
    public void testGetTargetConnectionFactoryWithMoreThanOneQueueException()
    {
        try
        {
            queueMasterLocatorConnectionFactory.getTargetConnectionFactory("testQueue, testQueue2");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Cannot use QueueMasterLocatorConnectionFactory with more than one queue: testQueue, testQueue2", e.getMessage());
        }
    }

    @Test
    public void testGetTargetConnectionFactoryWithConnectException()
    {
        when(defaultConnectionFactory.getHost()).thenReturn("localhost");
        when(defaultConnectionFactory.getUsername()).thenReturn("guest");

        ConnectionFactory resolved = queueMasterLocatorConnectionFactory.getTargetConnectionFactory("testQueue");

        verify(defaultConnectionFactory).getHost();
        verify(defaultConnectionFactory).getUsername();
        assertEquals(defaultConnectionFactory, resolved);
    }

    @Test
    public void testResolveMasterNodeForQueue()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(getQueueInfo());

        assertThat(masterNode).isEqualTo("localhost");
    }

    @Test
    public void testResolveMasterNodeForQueueWithoutQueueInfo()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(null);

        assertThat(masterNode).isNull();
    }

    @Test
    public void testResolveMasterNodeForQueueWithExclusive()
    {
        String masterNode = queueMasterLocatorConnectionFactory.resolveMasterNodeForQueue(getQueueInfoWithExclusive());

        assertThat(masterNode).isNull();
    }
}
