package com.r3signed.ac.regions.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class McUtils {
    /**
     * @return True if the current environment is a client, false if it is a server
     */
    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    /**
     * @return True if the current environment is a server, false if it is a client
     */
    public static boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }
}
