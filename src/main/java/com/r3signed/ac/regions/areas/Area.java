package com.r3signed.ac.regions.areas;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.UUID;

public abstract class Area {
    protected final UUID id;

    public Area() {
        // TODO: Check for existing UUIDs
        this(UUID.randomUUID());
    }

    public Area(UUID id) {
        this.id = id;
    }

    /**
     * @return The area ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return The area type
     */
    public abstract AreaType getType();

    /**
     * Checks if an area contains a {@link BlockPos}.
     *
     * @param pos The position to check
     */
    public boolean contains(BlockPos pos) {
        return contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * Checks if an area contains a {@link Vec3i}.
     *
     * @param pos The position to check
     */
    public boolean contains(Vec3i pos) {
        return contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }
    /**
     * Checks if an area contains a {@link Vec3d}.
     *
     * @param pos The position to check
     */
    public abstract boolean contains(Vec3d pos);

    /**
     * Checks if an area intersects with another area.
     *
     * @param other The other area to check
     */
    public abstract boolean intersects(Area other);
}
