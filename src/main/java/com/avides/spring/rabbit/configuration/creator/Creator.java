package com.avides.spring.rabbit.configuration.creator;

@FunctionalInterface
public interface Creator<I>
{
    I createInstance();
}
