package com.avides.spring.rabbit.test.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.avides.spring.rabbit.listener.CountingRabbitListener;

import lombok.Getter;

@Component
@Getter
public class TestClassListener extends CountingRabbitListener<TestClass>
{
    private List<TestClass> inbounds = new ArrayList<>();

    @Override
    protected void handleEvent(TestClass object)
    {
        inbounds.add(object);
    }
}
