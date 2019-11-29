package com.avides.spring.rabbit.configuration.creator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.provider.ConnectionFactoryProvider;
import com.avides.spring.rabbit.utils.DomainTestSupport;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConnectionFactoryProvider.class)
public class CustomConnectionFactoryCreatorTest implements DomainTestSupport
{
    private Creator<ConnectionFactory> creator = new CustomConnectionFactoryCreator(getCompleteCustomConnectionFactoryProperties());

    @Test
    public void testCreateInstance()
    {
        mockStatic(ConnectionFactoryProvider.class);
        ConnectionFactoryProvider.createConnectionFactory(anyObject(CachingConnectionFactory.class), anyObject(RabbitProperties.class), eq(17562));
        expectLastCall().andAnswer(() ->
        {
            CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) getCurrentArguments()[0];
            RabbitProperties rabbitProperties = (RabbitProperties) getCurrentArguments()[1];

            assertEquals("localhost", rabbitProperties.getAddresses());
            assertEquals("guest", rabbitProperties.getUsername());
            assertEquals("guest", rabbitProperties.getPassword());
            assertEquals("/IT", rabbitProperties.getVirtualHost());

            assertEquals("localhost", cachingConnectionFactory.getHost());
            assertEquals(5672, cachingConnectionFactory.getPort());
            assertEquals("guest", cachingConnectionFactory.getUsername());
            assertEquals("/IT", cachingConnectionFactory.getVirtualHost());

            return cachingConnectionFactory;
        });

        replayAll();
        creator.createInstance();
        verifyAll();
    }
}
