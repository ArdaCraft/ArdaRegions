package com.r3signed.ac.regions.core.areas;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.r3signed.ac.regions.internal.data.json.Json;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CuboidArea extends Area {
    private Vec3d min;
    private Vec3d max;

    @Override
    public AreaType getType() {
        return AreaType.CUBOID;
    }

    /**
     * @return A {@link Set} of the corners of the cuboid
     */
    public Set<Vec3d> getPoints() {
        if (min == null || max == null) {
            return Set.of();
        } else {
            return Set.of(
                    min,
                    new Vec3d(min.x, min.y, max.z),
                    new Vec3d(min.x, max.y, min.z),
                    new Vec3d(min.x, max.y, max.z),
                    new Vec3d(max.x, min.y, min.z),
                    new Vec3d(max.x, min.y, max.z),
                    new Vec3d(max.x, max.y, min.z),
                    max
            );
        }
    }

    /**
     * @return The minimum point of the cuboid
     */
    public @Nullable Vec3d getMin() {
        return min;
    }

    /**
     * @return The maximum point of the cuboid
     */
    public @Nullable Vec3d getMax() {
        return max;
    }

    /**
     * Sets the minimum or maximum point of the cuboid.
     * If both points are set, it will set the point that is closest to the origin.
     *
     * @param point The point to set
     */
    public void setPoint(Vec3d point) {
        if (min == null) {
            this.min = point;
        } else if (max == null) {
            this.max = point;
        } else if (point.x < min.x || point.y < min.y || point.z < min.z) {
            this.min = point;
        } else {
            this.max = point;
        }
    }

    @Override
    public boolean contains(Vec3d pos) {
        if (min == null || max == null) {
            return false;
        }

        return pos.x >= min.x && pos.x <= max.x && pos.y >= min.y && pos.y <= max.y && pos.z >= min.z && pos.z <= max.z;
    }

    @Override
    public boolean intersects(Area other) {
        if (min == null || max == null) {
            return false;
        }

        if (other instanceof CuboidArea cuboid) {
            return min.x <= cuboid.max.x
                    && max.x >= cuboid.min.x
                    && min.y <= cuboid.max.y
                    && max.y >= cuboid.min.y
                    && min.z <= cuboid.max.z
                    && max.z >= cuboid.min.z;
        } else if (other instanceof PolygonArea polygon) {
            for (Vec3d point : getPoints()) {
                if (polygon.contains(point)) {
                    return true;
                }
            }
            for (Vec3d point : polygon.getPoints()) {
                if (contains(point)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected Set<ChunkPos> calculateChunkMap() {
        if (min == null || max == null) {
            return Set.of();
        }

        int minChunkX = (int) Math.floor(min.x / 16);
        int minChunkZ = (int) Math.floor(min.z / 16);
        int maxChunkX = (int) Math.floor(max.x / 16);
        int maxChunkZ = (int) Math.floor(max.z / 16);

        Set<ChunkPos> chunks = new HashSet<>();
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                chunks.add(new ChunkPos(cx, cz));
            }
        }

        return chunks;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("min", Json.to(min));
        json.add("max", Json.to(max));
        return json;
    }

    @Override
    public CuboidArea fromJson(JsonObject json) {
        super.fromJson(json);
        if (!json.has("min") || !json.has("max")) {
            throw new JsonParseException("Invalid cuboid area JSON");
        }

        this.min = Json.from(json.get("min"), Vec3d.class);
        this.max = Json.from(json.get("max"), Vec3d.class);
        return this;
    }
}
