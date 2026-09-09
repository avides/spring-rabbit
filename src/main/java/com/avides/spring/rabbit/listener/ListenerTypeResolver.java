package com.avides.spring.rabbit.listener;

import java.lang.reflect.Type;

import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * Resolves the message-type a listener declares, i.e. the {@code T} of {@link SpringRabbitListener} or {@link RabbitListener}.
 * <p>
 * Walks the whole class-hierarchy instead of only looking at the direct superclass, and keeps the type-arguments of the message-type itself. Both matter for
 * listeners that are more than one class away from the listener-interface or whose message-type is generic - a listener-base binding
 * {@code AbstractSpringRabbitListener<Box<Integer>>}, for instance, has a message-type of {@code Box<Integer>}, which a converter needs in full to read a
 * {@code Box} of the right content.
 */
final class ListenerTypeResolver
{
    private ListenerTypeResolver()
    {
        // utility class
    }

    /**
     * The message-type the given listener-class declares for the given listener-interface.
     *
     * @param listenerClass the concrete listener-class
     * @param listenerInterface the interface declaring the message-type as its first type-parameter
     * @return the message-type, parameterized if it is
     */
    static Type resolveMessageType(Class<?> listenerClass, Class<?> listenerInterface)
    {
        var messageType = toType(ResolvableType.forClass(listenerClass).as(listenerInterface).getGeneric(0));
        Assert.state(messageType != null, () -> "could not resolve the message-type of " + listenerClass.getName() + " - it has to state one, e.g. by "
                + "extending AbstractSpringRabbitListener<MyMessage>");
        return messageType;
    }

    /**
     * The plain {@link Type} behind a {@link ResolvableType}. Not {@link ResolvableType#getType()}: that hands back the type-variable it was resolved from
     * ({@code T}), not what it resolved to. So the type is rebuilt from the resolved raw-class and its - recursively resolved - type-arguments, the way
     * {@code GenericTypeResolver} does it for Spring MVC's request-bodies.
     *
     * @param resolvableType the type to convert
     * @return the type, or <code>null</code> if it does not resolve at all
     */
    private static Type toType(ResolvableType resolvableType)
    {
        var rawClass = resolvableType.resolve();
        if (rawClass == null)
        {
            return null;
        }
        var generics = resolvableType.getGenerics();
        if (generics.length == 0)
        {
            return rawClass;
        }
        var resolvedGenerics = new ResolvableType[generics.length];
        for (var i = 0; i < generics.length; i++)
        {
            var resolvedGeneric = toType(generics[i]);
            if (resolvedGeneric == null)
            {
                // a type-argument that stays open - the raw class is all this message-type can offer
                return rawClass;
            }
            resolvedGenerics[i] = ResolvableType.forType(resolvedGeneric);
        }
        return ResolvableType.forClassWithGenerics(rawClass, resolvedGenerics).getType();
    }
}
