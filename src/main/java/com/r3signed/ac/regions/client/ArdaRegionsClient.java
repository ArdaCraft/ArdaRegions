package com.r3signed.ac.regions.client;

import com.r3signed.ac.regions.client.core.ClientServices;
import com.r3signed.ac.regions.client.rendering.RegionRenderer;
import com.r3signed.ac.regions.internal.network.ArdaRegionsS2CNetworking;
import net.fabricmc.api.ClientModInitializer;

public class ArdaRegionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientServices.init();
        RegionRenderer.init();
        ArdaRegionsS2CNetworking.init();
    }
}
