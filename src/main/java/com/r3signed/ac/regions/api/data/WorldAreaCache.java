package com.r3signed.ac.regions.api.data;

import com.r3signed.ac.regions.core.areas.Area;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface WorldAreaCache {
    /**
     * Returns the {@link World} this cache is for.
     *
     * @return A {@link World} object
     */
    World getWorld();
    /**
     * Returns all areas in this cache.
     *
     * @return A set of {@link Area} objects
     */
    Set<Area> getAreas();
    /**
     * Returns the area with the given ID.
     *
     * @param id The ID of the area
     * @return The {@link Area} object, or null if it doesn't exist
     */
    @Nullable
    Area getArea(UUID id);
    /**
     * Checks if the given area is in this cache.
     *
     * @param area The area to check for
     * @return True if the cache contains the area, otherwise false
     */
    boolean hasArea(Area area);
    /**
     * Checks if the given area ID is in this cache.
     *
     * @param id The ID of the area
     * @return True if the cache contains the area, otherwise false
     */
    boolean hasArea(UUID id);
    /**
     * Returns all areas that are ticking.
     *
     * @return A set of {@link Area} objects
     */
    Set<Area> getTickingAreas();
    /**
     * Checks if the given area is ticking.
     *
     * @param area The area to check for
     * @return True if the area is ticking, otherwise false
     */
    boolean isTicking(Area area);
    /**
     * Checks if the given area ID is ticking.
     *
     * @param id The ID of the area
     * @return True if the area is ticking, otherwise false
     */
    boolean isTicking(UUID id);
    /**
     * Adds an area to this cache and saves it.
     *
     * @param area The area to add
     */
    void add(Area area);
    /**
     * Updates an area in this cache and saves it.
     *
     * @param area The area to update
     */
    void update(Area area);
    /**
     * Removes an area from this cache and saves it.
     *
     * @param area The area to remove
     */
    void remove(Area area);
    /**
     * Removes an area from this cache and saves it.
     *
     * @param id The ID of the area
     */
    void remove(UUID id);
}
