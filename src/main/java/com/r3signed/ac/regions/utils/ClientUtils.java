package com.r3signed.ac.regions.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientUtils {
    /**
     * @return True if the client is in singleplayer, otherwise false
     */
    public static boolean isSinglePlayer() {
        return MinecraftClient.getInstance().isInSingleplayer();
    }

    /**
     * @return True if the client is on a server, otherwise false
     */
    public static boolean isOnServer() {
        if (isSinglePlayer()) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler != null) {
            return networkHandler.getConnection().isOpen();
        } else {
            return false;
        }
    }

    /**
     * Gets the host address of the server the client is connected to.
     *
     * @return The host address of the server, or null if the client is not connected to a server
     */
    public static @Nullable String getHostAddress() {
        if (isSinglePlayer()) {
            return null;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler != null) {
            SocketAddress socketAddress = networkHandler.getConnection().getAddress();
            if (socketAddress instanceof InetSocketAddress address) {
                return address.getHostString();
            }
        }

        return null;
    }
}
