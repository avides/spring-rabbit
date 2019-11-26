package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class ListenerPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteListenerProperties());
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(listenerProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(listenerProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(listenerProperties, "beanName");
    }

    // messageConverter
    @Test
    public void testBeanValidationOnMessageConverterWithNull()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setMessageConverter(null);
        BeanValidationTestSupport.expectNoError(listenerProperties);
    }

    @Test
    public void testBeanValidationOnMessageConverterWithInvalid()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.getMessageConverter().setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(listenerProperties, "messageConverter");
    }

    // maxConcurrentConsumers
    @Test
    public void testBeanValidationOnMaxConcurrentConsumersWithNull()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setMaxConcurrentConsumers(null);
        BeanValidationTestSupport.expectNoError(listenerProperties);
    }

    @Test
    public void testBeanValidationOnMaxConcurrentConsumersWithLessThanOne()
    {
        ListenerProperties listenerProperties = getCompleteListenerProperties();
        listenerProperties.setMaxConcurrentConsumers(Integer.valueOf(0));
        BeanValidationTestSupport.expectErrorOnlyOnProperty(listenerProperties, "maxConcurrentConsumers");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnCreationEnabled()
    {
        assertTrue(new ListenerProperties().isCreationEnabled());
    }

    @Test
    public void testDefaultValueOnBeanName()
    {
        assertNull(new ListenerProperties().getBeanName());
    }

    @Test
    public void testDefaultValueOnMessageConverter()
    {
        assertNull(new ListenerProperties().getMessageConverter());
    }

    @Test
    public void testDefaultValueOnMaxConcurrentConsumers()
    {
        assertNull(new ListenerProperties().getMaxConcurrentConsumers());
    }
}
