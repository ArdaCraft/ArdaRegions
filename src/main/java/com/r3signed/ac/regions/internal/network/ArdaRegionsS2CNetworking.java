package com.r3signed.ac.regions.internal.network;

import com.r3signed.ac.regions.internal.network.packet.AddCuboidPacket;
import com.r3signed.ac.regions.internal.network.packet.AddPolygonPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ArdaRegionsS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(AddCuboidPacket.TYPE, AddCuboidPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(AddPolygonPacket.TYPE, AddPolygonPacket::handlePacket);
    }

    public static void init() {
        // static initialisation
    }
}
