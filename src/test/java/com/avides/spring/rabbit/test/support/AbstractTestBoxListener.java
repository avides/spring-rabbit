package com.avides.spring.rabbit.test.support;

import com.avides.spring.rabbit.listener.AbstractSpringRabbitListener;

/**
 * A listener-base that binds a generic message-type and leaves only its content-type open - the shape a library builds on top of this one, so the concrete
 * listener only names its own domain-type.
 *
 * @param <E> type of the content of the incoming box
 */
public abstract class AbstractTestBoxListener<E> extends AbstractSpringRabbitListener<TestBox<E>>
{
    // nothing to do here
}
