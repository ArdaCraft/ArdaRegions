package com.r3signed.ac.regions.core.areas;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.r3signed.ac.regions.internal.data.json.Json;
import com.r3signed.ac.regions.internal.geometry.Triangle;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public class PolygonArea extends Area {
    private Set<Vec3d> points = Set.of();
    private Set<Triangle> faces = Set.of();

    @Override
    public AreaType getType() {
        return AreaType.POLYGON;
    }

    /**
     * @return The points of the polygon
     */
    public Set<Vec3d> getPoints() {
        return new HashSet<>(points);
    }

    /**
     * Sets the points of the polygon
     *
     * @param points The points to set
     */
    public void setPoints(Set<Vec3d> points) {
        this.points = Set.copyOf(points);
        this.faces = Triangle.computeFaces(points);
    }

    /**
     * @return The faces of the polygon
     */
    public Set<Triangle> getFaces() {
        return new HashSet<>(faces);
    }

    @Override
    public boolean contains(Vec3d pos) {
        if (points.size() < 4) {
            return false;
        }

        Vec3d rayDirection = new Vec3d(1.0, 0.00001, 0.00002).normalize();
        int crossings = 0;
        for (Triangle face : faces) {
            if (face.rayIntersects(pos, rayDirection)) {
                crossings++;
            }
        }

        return crossings % 2 == 1;
    }

    @Override
    public boolean intersects(Area other) {
        if (other instanceof PolygonArea polygon) {
            for (Vec3d point : points) {
                if (polygon.contains(point)) {
                    return true;
                }
            }
            for (Vec3d point : polygon.getPoints()) {
                if (contains(point)) {
                    return true;
                }
            }
        } else if (other instanceof CuboidArea cuboid) {
            for (Vec3d point : points) {
                if (cuboid.contains(point)) {
                    return true;
                }
            }
            for (Vec3d point : cuboid.getPoints()) {
                if (contains(point)) {
                    return true;
                }
            }
        }

        return false;
    }

    // TODO: Currently gets AABB and calculates chunks from that. This is not correct, but it works for now.
    @Override
    protected Set<ChunkPos> calculateChunkMap() {
        if (points.isEmpty()) {
            return Set.of();
        }

        double minX = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (Vec3d point : points) {
            minX = Math.min(minX, point.x);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxZ = Math.max(maxZ, point.z);
        }

        if (minX > maxX || minZ > maxZ) {
            return Set.of();
        }

        int minChunkX = (int) Math.floor(minX / 16);
        int minChunkZ = (int) Math.floor(minZ / 16);
        int maxChunkX = (int) Math.floor(maxX / 16);
        int maxChunkZ = (int) Math.floor(maxZ / 16);

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
        JsonArray pointsArray = new JsonArray();
        for (Vec3d points : points) {
            pointsArray.add(Json.to(points));
        }
        json.add("points", pointsArray);
        return json;
    }

    @Override
    public PolygonArea fromJson(JsonObject json) {
        super.fromJson(json);
        if (!json.has("points")) {
            throw new JsonParseException("Invalid polygon area JSON");
        }

        JsonArray pointsArray = json.getAsJsonArray("points");
        Set<Vec3d> points = new HashSet<>();
        for (JsonElement element : pointsArray) {
            points.add(Json.from(element, Vec3d.class));
        }
        setPoints(points);

        return this;
    }
}
