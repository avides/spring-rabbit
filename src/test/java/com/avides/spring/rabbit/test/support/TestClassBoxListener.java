package com.avides.spring.rabbit.test.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class TestClassBoxListener extends AbstractTestBoxListener<TestClass>
{
    private List<TestBox<TestClass>> inbounds = new ArrayList<>();

    @Override
    protected void handleEvent(TestBox<TestClass> object, MessageProperties messageProperties)
    {
        inbounds.add(object);
    }
}
