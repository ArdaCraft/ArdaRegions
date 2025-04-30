package com.r3signed.ac.regions.internal.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Enum representing the events that can be subscribed to.
 */
public enum Events {
    SERVER_WORLD_LOAD(ServerWorldEvents.LOAD, ServerWorldEvents.Load.class),
    SERVER_WORLD_UNLOAD(ServerWorldEvents.UNLOAD, ServerWorldEvents.Unload.class);

    private final Event<?> handler;
    private final Class<?> listener;
    private final Class<?>[] parameterTypes;

    <T> Events(Event<T> handler, Class<T> listener) {
        this.handler = handler;
        this.listener = listener;

        Method eventMethod = null;
        for (Method method : listener.getDeclaredMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                eventMethod = method;
                break;
            }
        }

        if (eventMethod == null) {
            throw new RuntimeException("No abstract method found in listener class \"" + listener.getName() + "\"");
        }

        this.parameterTypes = eventMethod.getParameterTypes();
    }

    /**
     * @return The event handler
     */
    public Event<?> getHandler() {
        return handler;
    }

    /**
     * @return The listener class
     */
    public Class<?> getListener() {
        return listener;
    }

    /**
     * @return The parameter types of the listener method
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
}
