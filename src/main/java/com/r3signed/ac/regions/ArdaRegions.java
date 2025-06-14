package com.r3signed.ac.regions;

import com.r3signed.ac.regions.core.ServerServices;
import com.r3signed.ac.regions.internal.command.CommandRegistration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
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
        CommandRegistration.init();
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
