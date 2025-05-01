package com.r3signed.ac.regions.client.core;

import com.r3signed.ac.regions.client.core.services.ClientAreaService;
import com.r3signed.ac.regions.internal.events.EventHandlerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientServices {
    public static final ClientAreaService AREAS = register(new ClientAreaService());

    /**
     * Registers a service.
     *
     * @param service The service to register
     */
    private static <T> T register(T service) {
        EventHandlerRegistry.register(service);
        return service;
    }

    public static void init() {}
}
