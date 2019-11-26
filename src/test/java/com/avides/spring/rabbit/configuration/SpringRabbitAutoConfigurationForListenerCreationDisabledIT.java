package com.avides.spring.rabbit.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.test.support.AbstractIT;

@ActiveProfiles({ "it", "listenerCreationDisabled" })
public class SpringRabbitAutoConfigurationForListenerCreationDisabledIT extends AbstractIT
{
    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.zero@springRabbitConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerZeroContainer;

    @Qualifier("dummyListenerOne@com.avides.spring.rabbit.queue.one@springRabbitConnectionFactory")
    // through the configuration no bean is added to the context
    @Autowired(required = false)
    private DefaultMessageListenerContainer<Object> dummyListenerOneContainer;

    @Test
    public void testAutoRabbitConfigurationForListenerCreationDisabled()
    {
        // check queues
        assertNotNull(applicationContext.getBean("com.avides.spring.rabbit.queue.zero_" + host, Queue.class));
        assertNotNull(applicationContext.getBean("com.avides.spring.rabbit.queue.zero.dlx_" + host, Queue.class));
        assertNotNull(applicationContext.getBean("com.avides.spring.rabbit.queue.one_" + host, Queue.class));
        assertNotNull(applicationContext.getBean("com.avides.spring.rabbit.queue.one.dlx_" + host, Queue.class));

        // check listener
        assertNotNull(dummyListenerZeroContainer);
        assertNull(dummyListenerOneContainer);
    }
}
