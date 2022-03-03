package com.avides.spring.rabbit.configuration.util;

import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveConnectionFactory;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveConnectionFactoryBeanName;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveExchange;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveMaxConcurrentConsumers;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveMessageConverter;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.support.GenericApplicationContext;

import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
public class DefaultValueResolverTest implements DomainTestSupport
{
    @MockStrict
    private GenericApplicationContext applicationContext;

    @MockStrict
    private MessageConverter jsonMessageConverter;

    @MockStrict
    private MessageConverter xmlMessageConverter;

    @MockStrict
    private ConnectionFactory connectionFactory;

    @Test
    public void testResolveExchangeWithCustomProperties()
    {
        assertNotNull(DefaultValueResolver.resolveExchange(getCompleteExchangeProperties(), null));
    }

    @Test
    public void testResolveExchangeWithDefaultPropertiesAndWithCustomProperties()
    {
        Exchange resolved = resolveExchange(getCompleteExchangeProperties("com.avides.custom"), getCompleteExchangeProperties("com.avides.default"));
        assertEquals("com.avides.custom", resolved.getName());
    }

    @Test
    public void testResolveExchangeWithDefaultPropertiesAndWithoutCustomProperties()
    {
        assertNotNull(DefaultValueResolver.resolveExchange(null, getCompleteExchangeProperties()));
    }

    @Test
    public void testResolveExchangeWithIllegalArgumentException()
    {
        try
        {
            DefaultValueResolver.resolveExchange(null, null);
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Could not resolve exchange", e.getMessage());
        }
    }

    @Test
    public void testResolveConnectionFactoryWithCustomProperties()
    {
        applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class);
        expectLastCall().andReturn(connectionFactory);

        replayAll();
        ConnectionFactory resolved = resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), null, applicationContext);
        verifyAll();

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithCustomPropertiesAndNotFound()
    {
        applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class);
        expectLastCall().andReturn(null);

        try
        {
            replayAll();
            resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), null, applicationContext);
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("QueueMasterLocatorConnectionFactory not found (BeanReferenceConnectionFactoryProperties(beanName=customConnectionFactoryBeanName))", e
                    .getMessage());
        }
    }

    @Test
    public void testResolveConnectionFactoryWithCustomPropertiesAndDefaultBeanName()
    {
        applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class);
        expectLastCall().andReturn(connectionFactory);

        replayAll();
        ConnectionFactory resolved = resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), "springRabbitConnectionFactory", applicationContext);
        verifyAll();

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithDefaultBeanName()
    {
        applicationContext.getBean("springRabbitConnectionFactory", ConnectionFactory.class);
        expectLastCall().andReturn(connectionFactory);

        replayAll();
        ConnectionFactory resolved = resolveConnectionFactory(null, "springRabbitConnectionFactory", applicationContext);
        verifyAll();

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithDefaultBeanNameAndNotFound()
    {
        applicationContext.getBean("springRabbitConnectionFactory", ConnectionFactory.class);
        expectLastCall().andReturn(null);

        try
        {
            replayAll();
            resolveConnectionFactory(null, "springRabbitConnectionFactory", applicationContext);
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("DefaultConnectionFactory not found (beanName:springRabbitConnectionFactory)", e.getMessage());
        }
    }

    @Test
    public void testResolveConnectionFactoryWithIllegalArgumentException()
    {
        try
        {
            replayAll();
            resolveConnectionFactory(null, null, applicationContext);
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Could not resolve the connectionFactory", e.getMessage());
        }
    }

    @Test
    public void testResolveConnectionFactoryBeanNameWithCustomPropertiesAndDefaultBeanName()
    {
        String beanName = resolveConnectionFactoryBeanName(getCompleteBeanReferenceConnectionFactoryProperties(), "springRabbitConnectionFactory");
        assertEquals("customConnectionFactoryBeanName", beanName);
    }

    @Test
    public void testResolveConnectionFactoryBeanNameWithDefaultBeanName()
    {
        String beanName = resolveConnectionFactoryBeanName(null, "springRabbitConnectionFactory");
        assertEquals("springRabbitConnectionFactory", beanName);
    }

    @Test
    public void testResolveMaxConcurrentConsumersWithCustomMax()
    {
        int resolved = resolveMaxConcurrentConsumers(Integer.valueOf(2), null);
        assertEquals(2, resolved);
    }

    @Test
    public void testResolveMaxConcurrentConsumersWithCustomMaxAndDefaultMax()
    {
        int resolved = resolveMaxConcurrentConsumers(Integer.valueOf(2), Integer.valueOf(1));
        assertEquals(2, resolved);
    }

    @Test
    public void testResolveMaxConcurrentConsumersWithDefaultMax()
    {
        int resolved = resolveMaxConcurrentConsumers(null, Integer.valueOf(1));
        assertEquals(1, resolved);
    }

    @Test
    public void testResolveValueWithCustomMax()
    {
        int resolved = resolveValue(Integer.valueOf(2), null);
        assertEquals(2, resolved);
    }

    @Test
    public void testResolveValueWithCustomMaxAndDefaultMax()
    {
        int resolved = resolveValue(Integer.valueOf(2), Integer.valueOf(1));
        assertEquals(2, resolved);
    }

    @Test
    public void testResolveValueWithDefaultMax()
    {
        int resolved = resolveValue(null, Integer.valueOf(1));
        assertEquals(1, resolved);
    }

    @Test
    public void testResolveMessageConverterWithCustomProperties()
    {
        applicationContext.getBean("xmlMarshallerBeanName", MessageConverter.class);
        expectLastCall().andReturn(xmlMessageConverter);

        replayAll();
        MessageConverter resolved = resolveMessageConverter(getCompleteMessageConverterProperties(), null, applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));
        verifyAll();

        assertEquals(xmlMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithCustomPropertiesAndNotFound()
    {
        applicationContext.getBean("unknownBeanName", MessageConverter.class);
        expectLastCall().andReturn(null);

        try
        {
            replayAll();
            resolveMessageConverter(getCompleteMessageConverterProperties("unknownBeanName"), null, applicationContext, Arrays
                    .asList(jsonMessageConverter, xmlMessageConverter));
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("CustomMessageConverter not found (MessageConverterProperties(beanName=unknownBeanName))", e.getMessage());
        }
    }

    @Test
    public void testResolveMessageConverterWithCustomPropertiesAndDefaultProperties()
    {
        applicationContext.getBean("xmlMarshallerBeanName", MessageConverter.class);
        expectLastCall().andReturn(xmlMessageConverter);

        replayAll();
        MessageConverter resolved = resolveMessageConverter(getCompleteMessageConverterProperties("xmlMarshallerBeanName"), getCompleteMessageConverterProperties("jsonMarshallerBeanName"), applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));
        verifyAll();

        assertEquals(xmlMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithDefaultProperties()
    {
        applicationContext.getBean("jsonMarshallerBeanName", MessageConverter.class);
        expectLastCall().andReturn(jsonMessageConverter);

        replayAll();
        MessageConverter resolved = resolveMessageConverter(null, getCompleteMessageConverterProperties("jsonMarshallerBeanName"), applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));
        verifyAll();

        assertEquals(jsonMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithDefaultPropertiesAndNotFound()
    {
        applicationContext.getBean("unknownBeanName", MessageConverter.class);
        expectLastCall().andReturn(null);

        try
        {
            replayAll();
            resolveMessageConverter(null, getCompleteMessageConverterProperties("unknownBeanName"), applicationContext, Arrays
                    .asList(jsonMessageConverter, xmlMessageConverter));
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("DefaultMessageConverter not found (MessageConverterProperties(beanName=unknownBeanName))", e.getMessage());
        }
    }

    @Test
    public void testResolveWithOneExistingMessageConverter()
    {
        replayAll();
        MessageConverter messageConverter = resolveMessageConverter(null, null, applicationContext, Arrays.asList(xmlMessageConverter));
        verifyAll();

        assertEquals(xmlMessageConverter, messageConverter);
    }

    @Test
    public void testResolveWithNotResolved()
    {
        try
        {
            replayAll();
            resolveMessageConverter(null, null, applicationContext, Arrays.asList(xmlMessageConverter, jsonMessageConverter));
            verifyAll();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Could not resolve messageConverter (existingMessageConverter size 2))", e.getMessage());
        }
    }
}
