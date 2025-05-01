package com.r3signed.ac.regions.core.areas;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.r3signed.ac.regions.internal.data.json.IJson;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public abstract class Area implements IJson<JsonObject, Area> {
    private UUID id;
    private Set<ChunkPos> chunks;

    public Area() {
        // TODO: Check for existing UUIDs
        this(UUID.randomUUID());
    }

    public Area(UUID id) {
        this.id = id;
    }

    /**
     * Creates an area from a JSON object.
     *
     * @param json The JSON object to create the area from
     * @return The area, or null if the type is not supported
     */
    public static @Nullable Area from(JsonObject json) {
        if (json.has("type")) {
            AreaType type = AreaType.valueOf(json.get("type").getAsString().toUpperCase());
            switch (type) {
                case CUBOID -> {
                    return new CuboidArea().fromJson(json);
                }
                case POLYGON -> {
                    return new PolygonArea().fromJson(json);
                }
            }
        }
        return null;
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
     * @return All chunks the area intersects with
     */
    public Set<ChunkPos> getChunks() {
        if (chunks == null) {
            this.chunks = calculateChunkMap();
        }
        return chunks;
    }

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

    /**
     * Checks if an area intersects with a chunk.
     *
     * @param chunk The chunk to check
     */
    public boolean intersects(Chunk chunk) {
        return intersects(chunk.getPos());
    }

    /**
     * Checks if an area intersects with a chunk.
     *
     * @param chunk The chunk to check
     */
    public boolean intersects(ChunkPos chunk) {
        return getChunks().contains(chunk);
    }

    /**
     * Calculates the chunks that the area intersects with.
     *
     * @return The set of chunk positions
     */
    protected abstract Set<ChunkPos> calculateChunkMap();

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id.toString());
        json.addProperty("type", getType().toString().toLowerCase());
        return json;
    }

    @Override
    public Area fromJson(JsonObject json) {
        if (!json.has("id") || !json.has("type")) {
            throw new JsonParseException("Invalid area JSON");
        }

        this.id = UUID.fromString(json.get("id").getAsString());
        return this;
    }
}
