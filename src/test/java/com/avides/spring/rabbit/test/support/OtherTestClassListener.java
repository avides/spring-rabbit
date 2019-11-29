package com.avides.spring.rabbit.test.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

import com.avides.spring.rabbit.listener.CountingContextAwareRabbitListener;

import lombok.Getter;

@Component
@Getter
public class OtherTestClassListener extends CountingContextAwareRabbitListener<OtherTestClass>
{
    private List<OtherTestClass> inbounds = new ArrayList<>();

    @Override
    protected void handleEvent(OtherTestClass object, MessageProperties messageProperties)
    {
        inbounds.add(object);
    }
}
