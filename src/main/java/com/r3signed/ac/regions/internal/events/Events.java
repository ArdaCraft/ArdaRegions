package com.r3signed.ac.regions.internal.events;

import com.r3signed.ac.regions.internal.events.world.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Enum representing the events that can be subscribed to.
 */
public enum Events {
    SERVER_WORLD_LOAD(ServerWorldEvents.LOAD, ServerWorldEvents.Load.class),
    SERVER_WORLD_UNLOAD(ServerWorldEvents.UNLOAD, ServerWorldEvents.Unload.class),
    SERVER_CHUNK_LOAD(ServerChunkEvents.CHUNK_LOAD, ServerChunkEvents.Load.class),
    SERVER_CHUNK_UNLOAD(ServerChunkEvents.CHUNK_UNLOAD, ServerChunkEvents.Unload.class),
    CLIENT_WORLD_LOAD(ClientWorldEvents.LOAD, ClientWorldEvents.Load.class),
    CLIENT_WORLD_UNLOAD(ClientWorldEvents.UNLOAD, ClientWorldEvents.Unload.class),
    CLIENT_CHUNK_LOAD(ClientChunkEvents.CHUNK_LOAD, ClientChunkEvents.Load.class),
    CLIENT_CHUNK_UNLOAD(ClientChunkEvents.CHUNK_UNLOAD, ClientChunkEvents.Unload.class);

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
