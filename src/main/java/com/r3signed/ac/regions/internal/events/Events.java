package com.r3signed.ac.regions.internal.events;

import com.r3signed.ac.regions.core.events.world.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

/**
 * Enum representing the events that can be subscribed to.
 */
public enum Events {
    /* Server */
    SERVER_WORLD_LOAD(()->ServerWorldEvents.LOAD, ()->ServerWorldEvents.Load.class),
    SERVER_WORLD_UNLOAD(()->ServerWorldEvents.UNLOAD, ()->ServerWorldEvents.Unload.class),
    SEVER_CHUNK_LOAD(()->ServerChunkEvents.CHUNK_LOAD, ()->ServerChunkEvents.Load.class),
    SERVER_CHUNK_UNLOAD(()->ServerChunkEvents.CHUNK_UNLOAD, ()->ServerChunkEvents.Unload.class),

    /* Client */
    CLIENT_WORLD_LOAD(()->ClientWorldEvents.LOAD, ()->ClientWorldEvents.Load.class),
    CLIENT_WORLD_UNLOAD(()->ClientWorldEvents.UNLOAD, ()->ClientWorldEvents.Unload.class),
    CLIENT_CHUNK_LOAD(()->ClientChunkEvents.CHUNK_LOAD, ()->ClientChunkEvents.Load.class),
    CLIENT_CHUNK_UNLOAD(()->ClientChunkEvents.CHUNK_UNLOAD, ()->ClientChunkEvents.Unload.class);

    // We provide these as suppliers to avoid loading client-side classes on the server
    private final Supplier<Event<?>> handler;
    private final Supplier<Class<?>> listener;
    private Class<?>[] parameterTypes;

    Events(Supplier<Event<?>> handler, Supplier<Class<?>> listener) {
        this.handler = handler;
        this.listener = listener;
    }

    /**
     * @return The event handler
     */
    public Event<?> getHandler() {
        return handler.get();
    }

    /**
     * @return The listener class
     */
    public Class<?> getListener() {
        return listener.get();
    }

    /**
     * @return The parameter types of the event method
     */
    public Class<?>[] getParameterTypes() {
        if (parameterTypes != null) {
            return parameterTypes;
        }

        Class<?> listener = getListener();
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
        return parameterTypes;
    }
}
