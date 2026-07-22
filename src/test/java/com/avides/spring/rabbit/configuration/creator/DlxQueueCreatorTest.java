package com.avides.spring.rabbit.configuration.creator;

import static com.avides.spring.rabbit.configuration.creator.QueueCreator.X_QUEUE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@ExtendWith(MockitoExtension.class)
public class DlxQueueCreatorTest implements DomainTestSupport
{
    private Creator<Queue> creator;

    @Mock
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testCreateInstanceWithDurable()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("quorum", queue.getArguments().get(X_QUEUE_TYPE).toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setDurable(true);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verify(rabbitAdmin).declareQueue(any(Queue.class));

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("quorum", dlxQueue.getArguments().get(X_QUEUE_TYPE).toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithNotDurable()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("quorum", queue.getArguments().get(X_QUEUE_TYPE).toString());
            assertFalse(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setDurable(false);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verify(rabbitAdmin).declareQueue(any(Queue.class));

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("quorum", dlxQueue.getArguments().get(X_QUEUE_TYPE).toString());
        assertFalse(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithExclusive()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("quorum", queue.getArguments().get(X_QUEUE_TYPE).toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertTrue(queue.isExclusive());
            return "testQueueName.dlx";
        });

        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExclusive(true);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verify(rabbitAdmin).declareQueue(any(Queue.class));

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("quorum", dlxQueue.getArguments().get(X_QUEUE_TYPE).toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertTrue(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithNotExclusive()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("quorum", queue.getArguments().get(X_QUEUE_TYPE).toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExclusive(false);
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verify(rabbitAdmin).declareQueue(any(Queue.class));

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("quorum", dlxQueue.getArguments().get(X_QUEUE_TYPE).toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }

    @Test
    public void testCreateInstanceWithOverriddenQueueType()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName.dlx", queue.getName());
            assertEquals(2, queue.getArguments().size());
            assertEquals("100", queue.getArguments().get("x-max-length").toString());
            assertEquals("classic", queue.getArguments().get(X_QUEUE_TYPE).toString());
            assertTrue(queue.isDurable());
            assertFalse(queue.isAutoDelete());
            assertFalse(queue.isExclusive());
            return "testQueueName.dlx";
        });

        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.getArguments().put(X_QUEUE_TYPE, "classic");
        creator = new DlxQueueCreator(rabbitAdmin, queueProperties);

        Queue dlxQueue = creator.createInstance();
        verify(rabbitAdmin).declareQueue(any(Queue.class));

        assertEquals("testQueueName.dlx", dlxQueue.getName());
        assertEquals(2, dlxQueue.getArguments().size());
        assertEquals("100", dlxQueue.getArguments().get("x-max-length").toString());
        assertEquals("classic", dlxQueue.getArguments().get(X_QUEUE_TYPE).toString());
        assertTrue(dlxQueue.isDurable());
        assertFalse(dlxQueue.isAutoDelete());
        assertFalse(dlxQueue.isExclusive());
    }
}
