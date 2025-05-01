package com.r3signed.ac.regions.client;

import com.r3signed.ac.regions.client.core.ClientServices;
import net.fabricmc.api.ClientModInitializer;

public class ArdaRegionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientServices.init();
    }
}
