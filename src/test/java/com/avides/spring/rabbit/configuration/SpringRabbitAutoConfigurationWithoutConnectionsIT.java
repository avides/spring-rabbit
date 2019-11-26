package com.avides.spring.rabbit.configuration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.avides.spring.rabbit.test.support.AbstractIT;
import com.avides.spring.rabbit.test.support.DummyListenerOne;
import com.avides.spring.rabbit.test.support.DummyListenerZero;

@SpringBootTest(classes = { SpringRabbitAutoConfigurationWithoutConnectionsIT.TestConfiguration.class, DummyListenerZero.class, DummyListenerOne.class })
public class SpringRabbitAutoConfigurationWithoutConnectionsIT extends AbstractIT
{
    @Autowired
    private ApplicationContext context;

    @Test
    public void testNoRabbitTemplateBeanAvailable()
    {
        assertThatThrownBy(() -> context.getBean(RabbitTemplate.class)).isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
    @Configuration
    static class TestConfiguration extends AbstractIT.TestConfiguration
    {
        // nothing, just to exclude RabbitAutoConfiguration
    }
}
