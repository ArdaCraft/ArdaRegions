package com.r3signed.ac.regions.internal.events;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * A class that implements {@link InvocationHandler} to handle method invocations on a listener.
 * This is used to create a proxy for the listener that can be passed to the event handler.
 */
public class ListenerInvocationHandler implements InvocationHandler {
    private final Object listener;
    private final Method method;

    public ListenerInvocationHandler(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.method.invoke(this.listener, args);
    }
}
