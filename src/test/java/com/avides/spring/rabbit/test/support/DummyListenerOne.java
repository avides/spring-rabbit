package com.avides.spring.rabbit.test.support;

import org.springframework.stereotype.Component;

import com.avides.spring.rabbit.listener.CountingRabbitListener;

@Component
public class DummyListenerOne extends CountingRabbitListener<String>
{
    @Override
    protected void handleEvent(String string)
    {
        // nothing
    }
}
