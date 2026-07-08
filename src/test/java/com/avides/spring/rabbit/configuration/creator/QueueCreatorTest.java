package com.avides.spring.rabbit.configuration.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@ExtendWith(MockitoExtension.class)
public class QueueCreatorTest implements DomainTestSupport
{
    private Creator<Queue> creator;

    @Mock
    private RabbitAdmin rabbitAdmin;

    @Mock
    private Exchange exchange;

    @Test
    public void testCreateInstance()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName", queue.getName());
            assertTrue(queue.isDurable());
            assertFalse(queue.isExclusive());
            assertFalse(queue.isAutoDelete());

            Map<String, Object> arguments = queue.getArguments();
            assertEquals(4, arguments.size());
            assertEquals("", arguments.get("x-dead-letter-exchange"));
            assertEquals("testQueueName.dlx", arguments.get("x-dead-letter-routing-key"));
            assertEquals("100", arguments.get("x-max-length").toString());
            assertEquals("additionalConfig", arguments.get("additionalArgument"));

            return "testQueueName";
        });

        lenient().when(exchange.getName()).thenReturn("springExchange");

        doAnswer(invocation ->
        {
            Binding binding = invocation.getArgument(0);
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        }).when(rabbitAdmin).declareBinding(any(Binding.class));

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "toOverride");
        arguments.put("x-dead-letter-routing-key", "toOverride");
        arguments.put("x-max-length", Long.valueOf(10000));
        arguments.put("additionalArgument", "additionalConfig");

        QueueProperties completeQueueProperties = getCompleteQueueProperties();
        completeQueueProperties.setArguments(arguments);
        creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
        creator.createInstance();

        verify(rabbitAdmin).declareQueue(any(Queue.class));
        verify(rabbitAdmin).declareExchange(exchange);
        verify(rabbitAdmin).declareBinding(any(Binding.class));
    }

    @Test
    public void testCreateInstanceWithMultipleRoutingKeys()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName", queue.getName());
            assertTrue(queue.isDurable());
            assertFalse(queue.isExclusive());
            assertFalse(queue.isAutoDelete());

            Map<String, Object> arguments = queue.getArguments();
            assertEquals(4, arguments.size());
            assertEquals("", arguments.get("x-dead-letter-exchange"));
            assertEquals("testQueueName.dlx", arguments.get("x-dead-letter-routing-key"));
            assertEquals("100", arguments.get("x-max-length").toString());
            assertEquals("additionalConfig", arguments.get("additionalArgument"));

            return "testQueueName";
        });

        lenient().when(exchange.getName()).thenReturn("springExchange");

        doAnswer(invocation ->
        {
            Binding binding = invocation.getArgument(0);
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        }).doAnswer(invocation ->
        {
            Binding binding = invocation.getArgument(0);
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("orders", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        }).when(rabbitAdmin).declareBinding(any(Binding.class));

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "toOverride");
        arguments.put("x-dead-letter-routing-key", "toOverride");
        arguments.put("x-max-length", Long.valueOf(10000));
        arguments.put("additionalArgument", "additionalConfig");

        QueueProperties completeQueueProperties = getCompleteQueueProperties();
        completeQueueProperties.setRoutingkey(null);
        completeQueueProperties.setRoutingkeys(Arrays.asList("product", "orders"));
        completeQueueProperties.setArguments(arguments);
        creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
        creator.createInstance();

        verify(rabbitAdmin).declareQueue(any(Queue.class));
        verify(rabbitAdmin).declareExchange(exchange);
    }

    @Test
    public void testCreateInstanceWithInvalidRoutingKey()
    {
        try
        {
            QueueProperties completeQueueProperties = getCompleteQueueProperties();
            completeQueueProperties.setRoutingkey("");
            creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
            creator.createInstance();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Either declare routing-keys or routing-key", e.getMessage());
        }
    }

    @Test
    public void testCreateInstanceWithInvalidRoutingKeys()
    {
        try
        {
            QueueProperties completeQueueProperties = getCompleteQueueProperties();
            completeQueueProperties.setRoutingkey(null);
            completeQueueProperties.setRoutingkeys(Arrays.asList(""));
            creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
            creator.createInstance();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Invalid routing-key", e.getMessage());
        }
    }

    @Test
    public void testCreateInstanceWithExclusive()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName", queue.getName());
            assertTrue(queue.isDurable());
            assertTrue(queue.isExclusive());
            assertFalse(queue.isAutoDelete());

            Map<String, Object> arguments = queue.getArguments();
            assertEquals(4, arguments.size());
            assertEquals("", arguments.get("x-dead-letter-exchange"));
            assertEquals("testQueueName.dlx", arguments.get("x-dead-letter-routing-key"));
            assertEquals("100", arguments.get("x-max-length").toString());
            assertEquals("additionalConfig", arguments.get("additionalArgument"));

            return "testQueueName";
        });

        lenient().when(exchange.getName()).thenReturn("springExchange");

        doAnswer(invocation ->
        {
            Binding binding = invocation.getArgument(0);
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        }).when(rabbitAdmin).declareBinding(any(Binding.class));

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "toOverride");
        arguments.put("x-dead-letter-routing-key", "toOverride");
        arguments.put("x-max-length", Long.valueOf(10000));
        arguments.put("additionalArgument", "additionalConfig");

        QueueProperties completeQueueProperties = getCompleteQueueProperties();
        completeQueueProperties.setExclusive(true);

        completeQueueProperties.setArguments(arguments);
        creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
        creator.createInstance();

        verify(rabbitAdmin).declareQueue(any(Queue.class));
        verify(rabbitAdmin).declareExchange(exchange);
        verify(rabbitAdmin).declareBinding(any(Binding.class));
    }

    @Test
    public void testCreateInstanceWithNotExclusive()
    {
        when(rabbitAdmin.declareQueue(any(Queue.class))).thenAnswer(invocation ->
        {
            Queue queue = invocation.getArgument(0);
            assertEquals("testQueueName", queue.getName());
            assertTrue(queue.isDurable());
            assertFalse(queue.isExclusive());
            assertFalse(queue.isAutoDelete());

            Map<String, Object> arguments = queue.getArguments();
            assertEquals(4, arguments.size());
            assertEquals("", arguments.get("x-dead-letter-exchange"));
            assertEquals("testQueueName.dlx", arguments.get("x-dead-letter-routing-key"));
            assertEquals("100", arguments.get("x-max-length").toString());
            assertEquals("additionalConfig", arguments.get("additionalArgument"));

            return "testQueueName";
        });

        lenient().when(exchange.getName()).thenReturn("springExchange");

        doAnswer(invocation ->
        {
            Binding binding = invocation.getArgument(0);
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        }).when(rabbitAdmin).declareBinding(any(Binding.class));

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "toOverride");
        arguments.put("x-dead-letter-routing-key", "toOverride");
        arguments.put("x-max-length", Long.valueOf(10000));
        arguments.put("additionalArgument", "additionalConfig");

        QueueProperties completeQueueProperties = getCompleteQueueProperties();
        completeQueueProperties.setExclusive(false);

        completeQueueProperties.setArguments(arguments);
        creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
        creator.createInstance();

        verify(rabbitAdmin).declareQueue(any(Queue.class));
        verify(rabbitAdmin).declareExchange(exchange);
        verify(rabbitAdmin).declareBinding(any(Binding.class));
    }
}
