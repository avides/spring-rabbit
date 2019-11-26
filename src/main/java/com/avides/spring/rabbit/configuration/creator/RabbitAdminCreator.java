package com.avides.spring.rabbit.configuration.creator;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RabbitAdminCreator implements Creator<RabbitAdmin>
{
    private final ConnectionFactory connectionFactory;

    @Override
    public RabbitAdmin createInstance()
    {
        return new RabbitAdmin(connectionFactory);
    }
}
