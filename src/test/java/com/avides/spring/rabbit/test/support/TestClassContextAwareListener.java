package com.avides.spring.rabbit.test.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

import com.avides.spring.rabbit.listener.CountingContextAwareRabbitListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Component
@Getter
public class TestClassContextAwareListener extends CountingContextAwareRabbitListener<TestClass>
{
    private List<Wrapper> inbounds = new ArrayList<>();

    @Override
    protected void handleEvent(TestClass object, MessageProperties messageProperties)
    {
        inbounds.add(new Wrapper(object, messageProperties));
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class Wrapper
    {
        private TestClass object;

        private MessageProperties messageProperties;
    }
}
