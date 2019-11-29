package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class QueuePropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteQueueProperties());
    }

    // name
    @Test
    public void testBeanValidationOnNameWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    @Test
    public void testBeanValidationOnNameWithEmpty()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    @Test
    public void testBeanValidationOnNameWithBlank()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "name");
    }

    // limit
    @Test
    public void testBeanValidationOnLimitWithLessThanOne()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setLimit(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "limit");
    }

    // exchange
    @Test
    public void testBeanValidationOnExchangeWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setExchange(null);
        BeanValidationTestSupport.expectNoError(queueProperties);
    }

    @Test
    public void testBeanValidationOnExchangeWithInvalid()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.getExchange().setName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "exchange");
    }

    // rabbitAdmin
    @Test
    public void testBeanValidationOnRabbitAdminWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setRabbitAdmin(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "rabbitAdmin");
    }

    // listener
    @Test
    public void testBeanValidationOnListenerWithNull()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.setListener(null);
        BeanValidationTestSupport.expectNoError(queueProperties);
    }

    @Test
    public void testBeanValidationOnListenerWithInvalid()
    {
        QueueProperties queueProperties = getCompleteQueueProperties();
        queueProperties.getListener().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(queueProperties, "listener");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnCreationEnabled()
    {
        assertTrue(new QueueProperties().isCreationEnabled());
    }

    @Test
    public void testDefaultValueOnRoutingkey()
    {
        assertNull(new QueueProperties().getRoutingkey());
    }

    @Test
    public void testDefaultValueOnRoutingkeys()
    {
        assertNull(new QueueProperties().getRoutingkeys());
    }

    @Test
    public void testDefaultValueOnName()
    {
        assertNull(new QueueProperties().getName());
    }

    @Test
    public void testDefaultValueOnLimit()
    {
        // throught bean-validation this will cause a beanValidationException so default value is ok because value should be configured
        assertEquals(0, new QueueProperties().getLimit());
    }

    @Test
    public void testDefaultValueOnDurable()
    {
        assertTrue(new QueueProperties().isDurable());
    }

    @Test
    public void testDefaultValueOnExclusive()
    {
        assertFalse(new QueueProperties().isExclusive());
    }

    @Test
    public void testDefaultValueOnArguments()
    {
        assertEquals(0, new QueueProperties().getArguments().size());
    }

    @Test
    public void testDefaultValueOnExchange()
    {
        assertNull(new QueueProperties().getExchange());
    }

    @Test
    public void testDefaultValueOnRabbitAdmin()
    {
        assertNotNull(new QueueProperties().getRabbitAdmin());
    }

    @Test
    public void testDefaultValueOnListener()
    {
        assertNull(new QueueProperties().getListener());
    }
}
