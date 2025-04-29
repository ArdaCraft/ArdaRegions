package com.r3signed.ac.regions.core.areas;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
        return pos.x >= Math.min(min.x, max.x) && pos.x <= Math.max(min.x, max.x) &&
                pos.y >= Math.min(min.y, max.y) && pos.y <= Math.max(min.y, max.y) &&
                pos.z >= Math.min(min.z, max.z) && pos.z <= Math.max(min.z, max.z);
    }

    @Override
    public boolean intersects(Area other) {
        if (other instanceof CuboidArea cuboid) {
            return this.min.x <= cuboid.max.x && this.max.x >= cuboid.min.x &&
                    this.min.y <= cuboid.max.y && this.max.y >= cuboid.min.y &&
                    this.min.z <= cuboid.max.z && this.max.z >= cuboid.min.z;
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
}
