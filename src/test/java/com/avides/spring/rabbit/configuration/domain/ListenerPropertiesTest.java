package com.avides.spring.rabbit.configuration.domain;

import static com.avides.spring.rabbit.utils.BeanValidationTestSupport.expectErrorOnlyOnProperty;
import static com.avides.spring.rabbit.utils.BeanValidationTestSupport.expectNoError;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.avides.spring.rabbit.utils.DomainTestSupport;

public class ListenerPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        expectNoError(getCompleteListenerProperties());
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.setBeanName(null)), "beanName");
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.setBeanName("")), "beanName");
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.setBeanName(" ")), "beanName");

        expectNoError(getCompleteListenerProperties(p -> p.setMessageConverter(null)));
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.getMessageConverter().setBeanName(null)), "messageConverter");

        expectNoError(getCompleteListenerProperties(p -> p.setPrefetchCount(null)));
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.setPrefetchCount(Integer.valueOf(0))), "prefetchCount");

        expectNoError(getCompleteListenerProperties(p -> p.setMaxConcurrentConsumers(null)));
        expectErrorOnlyOnProperty(getCompleteListenerProperties(p -> p.setMaxConcurrentConsumers(Integer.valueOf(0))), "maxConcurrentConsumers");
    }

    @Test
    public void testDefaultValues()
    {
        assertTrue(new ListenerProperties().isCreationEnabled());
        assertNull(new ListenerProperties().getBeanName());
        assertNull(new ListenerProperties().getMessageConverter());
        assertNull(new ListenerProperties().getPrefetchCount());
        assertNull(new ListenerProperties().getMaxConcurrentConsumers());
    }
}
