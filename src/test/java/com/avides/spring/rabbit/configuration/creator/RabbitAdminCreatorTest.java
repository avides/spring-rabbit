package com.avides.spring.rabbit.configuration.creator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

@ExtendWith(MockitoExtension.class)
public class RabbitAdminCreatorTest
{
    private Creator<RabbitAdmin> creator;

    @Mock
    private ConnectionFactory connectionFactory;

    @BeforeEach
    public void before()
    {
        creator = new RabbitAdminCreator(connectionFactory);
    }

    @Test
    public void testCreateInstance()
    {
        RabbitAdmin rabbitAdmin = creator.createInstance();

        assertNotNull(rabbitAdmin);
    }
}
