package com.avides.spring.rabbit.configuration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.listener.container.DefaultMessageListenerContainer;
import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.DummyListenerOne;
import com.avides.spring.rabbit.test.support.DummyListenerZero;

@ActiveProfiles({ "prefetchCount" })
@SpringBootTest(classes = { SpringRabbitAutoConfigurationForDifferentPrefetchCountIT.TestConfiguration.class, DummyListenerZero.class, DummyListenerOne.class })
public class SpringRabbitAutoConfigurationForDifferentPrefetchCountIT extends AbstractIT
{
    @Autowired
    private List<DefaultMessageListenerContainer<Object>> listenerContainer;

    @Qualifier("dummyListenerZero@com.avides.spring.rabbit.queue.zero@firstConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerZeroContainer;

    @Qualifier("dummyListenerOne@com.avides.spring.rabbit.queue.one@secondConnectionFactory")
    @Autowired
    private DefaultMessageListenerContainer<Object> dummyListenerOneContainer;

    @Test
    public void test()
    {
        assertEquals(2, listenerContainer.size());
        assertEquals(20, getPrefetchCount(dummyListenerZeroContainer));
        assertEquals(4, getPrefetchCount(dummyListenerOneContainer));
    }

    @EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
    @Configuration
    static class TestConfiguration extends AbstractIT.TestConfiguration
    {
        // nothing, just to exclude RabbitAutoConfiguration
    }
}
