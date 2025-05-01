package com.r3signed.ac.regions.utils;

import java.io.File;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class WorldUtils {
    /**
     * Gets a world's unique key.
     *
     * @param world The world to get the key from
     * @return The unique key of the world
     */
    public static String getKey(World world) {
        return world.getRegistryKey().getValue().toString();
    }

    /**
     * Get the save folder for a world.
     */
    public static @Nullable File getFolder(World world) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            Path worldRoot = server.getSavePath(WorldSavePath.ROOT);
            Path worldPath = DimensionType.getSaveDirectory(world.getRegistryKey(), worldRoot);
            return worldPath.toFile();
        } else {
            return null;
        }
    }
}
