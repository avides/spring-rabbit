package com.avides.spring.rabbit.configuration.creator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

@ExtendWith(MockitoExtension.class)
class RabbitAdminCreatorTest
{
    @Mock
    private ConnectionFactory connectionFactory;

    @InjectMocks
    private RabbitAdminCreator creator;

    @Test
    void testCreateInstance()
    {
        RabbitAdmin rabbitAdmin = creator.createInstance();

        assertNotNull(rabbitAdmin);
    }
}
