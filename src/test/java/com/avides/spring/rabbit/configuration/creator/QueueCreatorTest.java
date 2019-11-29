package com.avides.spring.rabbit.configuration.creator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
public class QueueCreatorTest implements DomainTestSupport
{
    private Creator<Queue> creator;

    @MockStrict
    private RabbitAdmin rabbitAdmin;

    @MockStrict
    private Exchange exchange;

    @Test
    public void testCreateInstance()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
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

        rabbitAdmin.declareExchange(exchange);

        exchange.getName();
        expectLastCall().andReturn("springExchange");

        rabbitAdmin.declareBinding(anyObject(Binding.class));
        expectLastCall().andAnswer(() ->
        {
            Binding binding = (Binding) getCurrentArguments()[0];
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        });

        replayAll();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "toOverride");
        arguments.put("x-dead-letter-routing-key", "toOverride");
        arguments.put("x-max-length", Long.valueOf(10000));
        arguments.put("additionalArgument", "additionalConfig");

        QueueProperties completeQueueProperties = getCompleteQueueProperties();
        completeQueueProperties.setArguments(arguments);
        creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
        creator.createInstance();
        verifyAll();
    }

    @Test
    public void testCreateInstanceWithMultipleRoutingKeys()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
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

        rabbitAdmin.declareExchange(exchange);

        exchange.getName();
        expectLastCall().andReturn("springExchange");

        rabbitAdmin.declareBinding(anyObject(Binding.class));
        expectLastCall().andAnswer(() ->
        {
            Binding binding = (Binding) getCurrentArguments()[0];
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        });

        exchange.getName();
        expectLastCall().andReturn("springExchange");

        rabbitAdmin.declareBinding(anyObject(Binding.class));
        expectLastCall().andAnswer(() ->
        {
            Binding binding = (Binding) getCurrentArguments()[0];
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("orders", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        });

        replayAll();
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
        verifyAll();

    }

    @Test
    public void testCreateInstanceWithInvalidRoutingKey()
    {
        try
        {
            replayAll();
            QueueProperties completeQueueProperties = getCompleteQueueProperties();
            completeQueueProperties.setRoutingkey("");
            creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
            creator.createInstance();
            verifyAll();
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
            replayAll();
            QueueProperties completeQueueProperties = getCompleteQueueProperties();
            completeQueueProperties.setRoutingkey(null);
            completeQueueProperties.setRoutingkeys(Arrays.asList(""));
            creator = new QueueCreator(completeQueueProperties, rabbitAdmin, exchange);
            creator.createInstance();
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Invalid routing-key", e.getMessage());
        }
    }

    @Test
    public void testCreateInstanceWithExclusive()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
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

        rabbitAdmin.declareExchange(exchange);

        exchange.getName();
        expectLastCall().andReturn("springExchange");

        rabbitAdmin.declareBinding(anyObject(Binding.class));
        expectLastCall().andAnswer(() ->
        {
            Binding binding = (Binding) getCurrentArguments()[0];
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        });

        replayAll();
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
        verifyAll();
    }

    @Test
    public void testCreateInstanceWithNotExclusive()
    {
        rabbitAdmin.declareQueue(anyObject(Queue.class));
        expectLastCall().andAnswer(() ->
        {
            Queue queue = (Queue) getCurrentArguments()[0];
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

        rabbitAdmin.declareExchange(exchange);

        exchange.getName();
        expectLastCall().andReturn("springExchange");

        rabbitAdmin.declareBinding(anyObject(Binding.class));
        expectLastCall().andAnswer(() ->
        {
            Binding binding = (Binding) getCurrentArguments()[0];
            assertTrue(binding.getArguments().isEmpty());
            assertEquals("product", binding.getRoutingKey());
            assertEquals("springExchange", binding.getExchange());
            return null;
        });

        replayAll();
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
        verifyAll();
    }
}
