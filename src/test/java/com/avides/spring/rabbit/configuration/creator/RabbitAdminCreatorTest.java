package com.avides.spring.rabbit.configuration.creator;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

@RunWith(PowerMockRunner.class)
public class RabbitAdminCreatorTest
{
    private Creator<RabbitAdmin> creator;

    @MockStrict
    private ConnectionFactory connectionFactory;

    @Before
    public void before()
    {
        creator = new RabbitAdminCreator(connectionFactory);
    }

    @Test
    public void testCreateInstance()
    {
        replayAll();
        RabbitAdmin rabbitAdmin = creator.createInstance();
        verifyAll();

        assertNotNull(rabbitAdmin);
    }
}
