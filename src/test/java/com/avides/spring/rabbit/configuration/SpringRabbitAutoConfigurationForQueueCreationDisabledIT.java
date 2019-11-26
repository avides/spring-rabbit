package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.test.support.AbstractIT;

@ActiveProfiles({ "it", "queueCreationDisabled" })
public class SpringRabbitAutoConfigurationForQueueCreationDisabledIT extends AbstractIT
{
    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.zero@springRabbitConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerZeroContainer;

    @Qualifier("dummyListenerOne@com.avides.spring.rabbit.queue.one@springRabbitConnectionFactory")
    // through the configuration no bean is added to the context
    @Autowired(required = false)
    private DefaultMessageListenerContainer<Object> dummyListenerOneContainer;

    @Test
    public void testAutoRabbitConfigurationForQueueCreationDisabled()
    {
        // check queues
        applicationContext.getBean("com.avides.spring.rabbit.queue.zero_" + host, Queue.class);
        applicationContext.getBean("com.avides.spring.rabbit.queue.zero.dlx_" + host, Queue.class);

        assertThatThrownBy(() -> applicationContext.getBean("com.avides.spring.rabbit.queue.one_" + host, Queue.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'com.avides.spring.rabbit.queue.one_" + host + "' available");

        assertThatThrownBy(() -> applicationContext.getBean("com.avides.spring.rabbit.queue.one.dlx_" + host, Queue.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'com.avides.spring.rabbit.queue.one.dlx_" + host + "' available");

        // check listener
        assertNotNull(dummyListenerZeroContainer);
        assertNull(dummyListenerOneContainer);
    }
}
