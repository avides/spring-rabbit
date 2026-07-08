package com.avides.spring.rabbit.configuration.util;

import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveConnectionFactory;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveConnectionFactoryBeanName;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveExchange;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveMaxConcurrentConsumers;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveMessageConverter;
import static com.avides.spring.rabbit.configuration.util.DefaultValueResolver.resolveValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.support.GenericApplicationContext;

import com.avides.spring.rabbit.utils.DomainTestSupport;

@ExtendWith(MockitoExtension.class)
public class DefaultValueResolverTest implements DomainTestSupport
{
    @Mock
    private GenericApplicationContext applicationContext;

    @Mock
    private MessageConverter jsonMessageConverter;

    @Mock
    private MessageConverter xmlMessageConverter;

    @Mock
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
        when(applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class)).thenReturn(connectionFactory);

        ConnectionFactory resolved = resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), null, applicationContext);

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithCustomPropertiesAndNotFound()
    {
        when(applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class)).thenReturn(null);

        try
        {
            resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), null, applicationContext);
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
        when(applicationContext.getBean("customConnectionFactoryBeanName", ConnectionFactory.class)).thenReturn(connectionFactory);

        ConnectionFactory resolved = resolveConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties(), "springRabbitConnectionFactory", applicationContext);

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithDefaultBeanName()
    {
        when(applicationContext.getBean("springRabbitConnectionFactory", ConnectionFactory.class)).thenReturn(connectionFactory);

        ConnectionFactory resolved = resolveConnectionFactory(null, "springRabbitConnectionFactory", applicationContext);

        assertNotNull(resolved);
    }

    @Test
    public void testResolveConnectionFactoryWithDefaultBeanNameAndNotFound()
    {
        when(applicationContext.getBean("springRabbitConnectionFactory", ConnectionFactory.class)).thenReturn(null);

        try
        {
            resolveConnectionFactory(null, "springRabbitConnectionFactory", applicationContext);
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
            resolveConnectionFactory(null, null, applicationContext);
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
        when(applicationContext.getBean("xmlMarshallerBeanName", MessageConverter.class)).thenReturn(xmlMessageConverter);

        MessageConverter resolved = resolveMessageConverter(getCompleteMessageConverterProperties(), null, applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));

        assertEquals(xmlMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithCustomPropertiesAndNotFound()
    {
        when(applicationContext.getBean("unknownBeanName", MessageConverter.class)).thenReturn(null);

        try
        {
            resolveMessageConverter(getCompleteMessageConverterProperties("unknownBeanName"), null, applicationContext, Arrays
                    .asList(jsonMessageConverter, xmlMessageConverter));
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("CustomMessageConverter not found (MessageConverterProperties(beanName=unknownBeanName))", e.getMessage());
        }
    }

    @Test
    public void testResolveMessageConverterWithCustomPropertiesAndDefaultProperties()
    {
        when(applicationContext.getBean("xmlMarshallerBeanName", MessageConverter.class)).thenReturn(xmlMessageConverter);

        MessageConverter resolved = resolveMessageConverter(getCompleteMessageConverterProperties("xmlMarshallerBeanName"), getCompleteMessageConverterProperties("jsonMarshallerBeanName"), applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));

        assertEquals(xmlMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithDefaultProperties()
    {
        when(applicationContext.getBean("jsonMarshallerBeanName", MessageConverter.class)).thenReturn(jsonMessageConverter);

        MessageConverter resolved = resolveMessageConverter(null, getCompleteMessageConverterProperties("jsonMarshallerBeanName"), applicationContext, Arrays
                .asList(jsonMessageConverter, xmlMessageConverter));

        assertEquals(jsonMessageConverter, resolved);
    }

    @Test
    public void testResolveMessageConverterWithDefaultPropertiesAndNotFound()
    {
        when(applicationContext.getBean("unknownBeanName", MessageConverter.class)).thenReturn(null);

        try
        {
            resolveMessageConverter(null, getCompleteMessageConverterProperties("unknownBeanName"), applicationContext, Arrays
                    .asList(jsonMessageConverter, xmlMessageConverter));
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("DefaultMessageConverter not found (MessageConverterProperties(beanName=unknownBeanName))", e.getMessage());
        }
    }

    @Test
    public void testResolveWithOneExistingMessageConverter()
    {
        MessageConverter messageConverter = resolveMessageConverter(null, null, applicationContext, Arrays.asList(xmlMessageConverter));

        assertEquals(xmlMessageConverter, messageConverter);
    }

    @Test
    public void testResolveWithNotResolved()
    {
        try
        {
            resolveMessageConverter(null, null, applicationContext, Arrays.asList(xmlMessageConverter, jsonMessageConverter));
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Could not resolve messageConverter (existingMessageConverter size 2))", e.getMessage());
        }
    }
}
