package com.r3signed.ac.regions.core;

import com.r3signed.ac.regions.core.services.ServerAreaService;
import com.r3signed.ac.regions.internal.events.EventHandlerRegistry;

public class ServerServices {
    public static final ServerAreaService AREAS = register(new ServerAreaService());

    /**
     * Registers a service.
     *
     * @param service The service to register
     */
    private static <T> T register(T service) {
        EventHandlerRegistry.register(service);
        return service;
    }

    /**
     * Called by the main mod class to ensure initialization.
     */
    public static void init() {}
}
