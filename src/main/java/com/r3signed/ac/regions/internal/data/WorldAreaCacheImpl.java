package com.r3signed.ac.regions.internal.data;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.core.events.areas.AreaTickEvents;
import com.r3signed.ac.regions.internal.concurrency.WorkerThread;
import com.r3signed.ac.regions.utils.WorldUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class WorldAreaCacheImpl implements WorldAreaCache {
    protected final World world;
    protected final Set<Area> areas;
    private final Set<Area> ticking;
    private final Set<ChunkPos> loadedChunks;

    public WorldAreaCacheImpl(World world) {
        this.world = world;
        this.areas = load();
        this.ticking = Collections.newSetFromMap(new WeakHashMap<>());
        this.loadedChunks = new HashSet<>();
        ArdaRegions.LOGGER.info("Loaded {} areas for level \"{}\"", areas.size(), WorldUtils.getKey(world));
    }

    /**
     * @return The world this cache is for
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * @return The areas this cache contains
     */
    @Override
    public Set<Area> getAreas() {
        return areas;
    }

    /**
     * Gets an area by its ID.
     *
     * @param id The ID of the area
     * @return The area with the given ID, or null if it doesn't exist
     */
    @Override
    public @Nullable Area getArea(UUID id) {
        return areas.stream()
                .filter(area -> area.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return True if the area is in this cache
     */
    @Override
    public boolean hasArea(Area area) {
        return areas.contains(area);
    }

    /**
     * @return True if the area is in this cache
     */
    @Override
    public boolean hasArea(UUID id) {
        return areas.stream().anyMatch(area -> area.getId().equals(id));
    }

    /**
     * @return The areas that are currently ticking
     */
    @Override
    public Set<Area> getTickingAreas() {
        return ticking;
    }

    /**
     * @return True if the area is currently ticking
     */
    @Override
    public boolean isTicking(Area area) {
        return ticking.contains(area);
    }

    /**
     * @return True if the area is currently ticking
     */
    @Override
    public boolean isTicking(UUID id) {
        return ticking.stream().anyMatch(area -> area.getId().equals(id));
    }

    /**
     * Adds an area to the cache.
     *
     * @param area The area to add
     */
    @Override
    public void add(Area area) {
        if (areas.contains(area)) {
            return;
        }

        areas.add(area);
        for (ChunkPos areaChunk : area.getChunks()) {
            if (loadedChunks.contains(areaChunk)) {
                ticking.add(area);
                break;
            }
        }

        save();
    }

    /**
     * Updates an area in the cache.
     *
     * @param area The area to update
     */
    @Override
    public void update(Area area) {
        areas.removeIf(a -> a.getId().equals(area.getId()));
        add(area);
    }

    /**
     * Removes an area from the cache.
     *
     * @param area The area to remove
     */
    @Override
    public void remove(Area area) {
        remove(area.getId());
    }

    /**
     * Removes an area from the cache.
     *
     * @param id The id of the area to remove
     */
    @Override
    public void remove(UUID id) {
        boolean updated = areas.removeIf(area -> area.getId().equals(id));
        if (updated) {
            save();
        }
    }

    /**
     * Called when a chunk is loaded. Adds an area to the ticking list
     * if it intersects with the chunk.
     *
     * @param chunk The chunk that was loaded
     */
    public void onChunkLoad(ChunkPos chunk) {
        if (!loadedChunks.contains(chunk)) {
            loadedChunks.add(chunk);
            WorkerThread.execute(() -> {
                for (Area area : areas) {
                    if (!ticking.contains(area) && area.intersects(chunk)) {
                        ticking.add(area);
                        AreaTickEvents.START.invoker().onAreaStartTicking(area);
                    }
                }
            });
        }
    }

    /**
     * Called when a chunk is unloaded. Removes an area from the ticking
     * list if it no longer intersects with any loaded chunks.
     *
     * @param chunk The chunk that was unloaded
     */
    public void onChunkUnload(ChunkPos chunk) {
        if (loadedChunks.remove(chunk)) {
            WorkerThread.execute(() -> {
                for (Area area : areas) {
                    if (!ticking.contains(area)) {
                        continue;
                    }

                    if (area.intersects(chunk)) {
                        Set<ChunkPos> areaChunks = area.getChunks();
                        boolean unload = true;
                        for (ChunkPos areaChunk : areaChunks) {
                            if (loadedChunks.contains(areaChunk)) {
                                unload = false;
                                break;
                            }
                        }

                        if (unload) {
                            ticking.remove(area);
                            AreaTickEvents.STOP.invoker().onAreaStopTicking(area);
                        }
                    }
                }
            });
        }
    }

    /**
     * Saves the cache.
     */
    protected abstract void save();

    /**
     * Loads the cache.
     *
     * @return The areas in the cache
     */
    protected abstract Set<Area> load();
}
