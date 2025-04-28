package com.r3signed.ac.regions.areas;

import com.r3signed.ac.regions.internal.geometry.Triangle;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

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
}
