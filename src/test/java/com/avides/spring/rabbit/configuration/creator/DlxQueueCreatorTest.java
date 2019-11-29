package com.avides.spring.rabbit.configuration.creator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
public class DlxQueueCreatorTest implements DomainTestSupport
{
    private Creator<Queue> creator;

    @MockStrict
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testCreateInstanceWithDurable()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("lazy", queue.getArguments().get("x-queue-mode").toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        replayAll();
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setDurable(true);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verifyAll();

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("lazy", dlxQueue.getArguments().get("x-queue-mode").toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithNotDurable()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("lazy", queue.getArguments().get("x-queue-mode").toString());
            assertFalse(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        replayAll();
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setDurable(false);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verifyAll();

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("lazy", dlxQueue.getArguments().get("x-queue-mode").toString());
        assertFalse(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithExclusive()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("lazy", queue.getArguments().get("x-queue-mode").toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertTrue(queue.isExclusive());
            return "testQueueName.dlx";
        });

        replayAll();
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExclusive(true);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verifyAll();

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("lazy", dlxQueue.getArguments().get("x-queue-mode").toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertTrue(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithNotExclusive()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("lazy", queue.getArguments().get("x-queue-mode").toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        replayAll();
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExclusive(false);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verifyAll();

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("lazy", dlxQueue.getArguments().get("x-queue-mode").toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }
}
