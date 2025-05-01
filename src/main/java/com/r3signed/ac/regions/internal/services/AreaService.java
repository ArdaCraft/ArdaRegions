package com.r3signed.ac.regions.internal.services;

import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.client.internal.data.ClientWorldAreaCache;
import com.r3signed.ac.regions.internal.concurrency.WorkerThread;
import com.r3signed.ac.regions.internal.data.ServerWorldAreaCache;
import com.r3signed.ac.regions.internal.data.WorldAreaCacheImpl;
import com.r3signed.ac.regions.utils.McUtils;
import java.util.HashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AreaService {
    private final HashMap<World, WorldAreaCacheImpl> worlds = new HashMap<>();

    /**
     * @return The world area cache for the given world, or null if it doesn't exist
     */
    public @Nullable WorldAreaCache getCache(World world) {
        return worlds.get(world);
    }

    /**
     * Called when a world is loaded.
     *
     * @param world The world that was loaded
     */
    protected void onWorldLoad(World world) {
        WorkerThread.execute(() -> {
            WorldAreaCacheImpl cache;
            if (McUtils.isServer()) {
                cache = new ServerWorldAreaCache(world);
            } else {
                cache = new ClientWorldAreaCache(world);
            }
            worlds.put(world, cache);
        });
    }

    /**
     * Called when a world is unloaded.
     *
     * @param world The world that was unloaded
     */
    protected void onWorldUnload(World world) {
        worlds.remove(world);
    }

    /**
     * Called when a chunk is loaded.
     *
     * @param world The world the chunk is in
     * @param chunkPos The position of the chunk
     */
    protected void onChunkLoad(World world, ChunkPos chunkPos) {
        WorldAreaCacheImpl cache = worlds.get(world);
        if (cache != null) {
            cache.onChunkLoad(chunkPos);
        }
    }

    /**
     * Called when a chunk is unloaded.
     *
     * @param world The world the chunk is in
     * @param chunkPos The position of the chunk
     */
    protected void onChunkUnload(World world, ChunkPos chunkPos) {
        WorldAreaCacheImpl cache = worlds.get(world);
        if (cache != null) {
            cache.onChunkUnload(chunkPos);
        }
    }
}
