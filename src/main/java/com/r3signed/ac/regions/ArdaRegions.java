package com.r3signed.ac.regions;

import com.r3signed.ac.regions.core.ServerServices;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ArdaRegions implements ModInitializer {
    public static final String MOD_ID = "arda-regions";
    public static final String MOD_NAME = "Arda Regions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final File MOD_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), MOD_ID);

    @Override
    public void onInitialize() {
        ServerServices.init();
    }
}
