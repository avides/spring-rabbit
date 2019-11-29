package com.avides.spring.rabbit.configuration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
public class QueueMasterLocatorConnectionFactoryTest implements DomainTestSupport
{
    private QueueMasterLocatorConnectionFactory queueMasterLocatorConnectionFactory;

    @MockStrict
    private ConnectionFactory defaultConnectionFactory;

    @Before
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
        defaultConnectionFactory.getHost();
        expectLastCall().andReturn("localhost");

        defaultConnectionFactory.getUsername();
        expectLastCall().andReturn("guest");

        replayAll();
        ConnectionFactory resolved = queueMasterLocatorConnectionFactory.getTargetConnectionFactory("testQueue");
        verifyAll();

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
