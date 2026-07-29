package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class QueuePropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteQueueProperties());
    }

    // name
    @Test
    void testBeanValidationOnNameWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    @Test
    void testBeanValidationOnNameWithEmpty()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    @Test
    void testBeanValidationOnNameWithBlank()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    // limit
    @Test
    void testBeanValidationOnLimitWithLessThanOne()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setLimit(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "limit");
    }

    // exchange
    @Test
    void testBeanValidationOnExchangeWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExchange(null);
        BeanValidationTestSupport.expectNoError(queueProperties);
    }

    @Test
    void testBeanValidationOnExchangeWithInvalid()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "exchange");
    }

    // rabbitAdmin
    @Test
    void testBeanValidationOnRabbitAdminWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setRabbitAdmin(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "rabbitAdmin");
    }

    // listener
    @Test
    void testBeanValidationOnListenerWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setListener(null);
        BeanValidationTestSupport.expectNoError(queueProperties);
    }

    @Test
    void testBeanValidationOnListenerWithInvalid()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.getListener().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "listener");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnCreationEnabled()
    {
        assertTrue(new QueueProperties().isCreationEnabled());
    }

    @Test
    void testDefaultValueOnRoutingkey()
    {
        assertNull(new QueueProperties().getRoutingkey());
    }

    @Test
    void testDefaultValueOnRoutingkeys()
    {
        assertNull(new QueueProperties().getRoutingkeys());
    }

    @Test
    void testDefaultValueOnName()
    {
        assertNull(new QueueProperties().getName());
    }

    @Test
    void testDefaultValueOnLimit()
    {
        // throught bean-validation this will cause a beanValidationException so default value is ok because value should be configured
        assertEquals(0, new QueueProperties().getLimit());
    }

    @Test
    void testDefaultValueOnDurable()
    {
        assertTrue(new QueueProperties().isDurable());
    }

    @Test
    void testDefaultValueOnExclusive()
    {
        assertFalse(new QueueProperties().isExclusive());
    }

    @Test
    void testDefaultValueOnArguments()
    {
        assertEquals(0, new QueueProperties().getArguments().size());
    }

    @Test
    void testDefaultValueOnExchange()
    {
        assertNull(new QueueProperties().getExchange());
    }

    @Test
    void testDefaultValueOnRabbitAdmin()
    {
        assertNotNull(new QueueProperties().getRabbitAdmin());
    }

    @Test
    void testDefaultValueOnListener()
    {
        assertNull(new QueueProperties().getListener());
    }
}
