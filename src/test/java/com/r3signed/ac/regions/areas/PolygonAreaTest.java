package com.r3signed.ac.regions.areas;

import com.r3signed.ac.regions.core.areas.CuboidArea;
import com.r3signed.ac.regions.core.areas.PolygonArea;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolygonAreaTest {
    private PolygonArea area;

    @BeforeEach
    public void setup() {
        area = new PolygonArea();
        // Pyramid
        area.setPoints(Set.of(
                new Vec3d(0, 0, 0),
                new Vec3d(10, 0, 0),
                new Vec3d(0, 0, 10),
                new Vec3d(10, 0, 10),
                new Vec3d(5, 10, 5)
        ));
    }

    @Nested
    @DisplayName("containsSync Tests")
    public class ContainsTests {
        @Test
        @DisplayName("Point inside")
        public void testPointInside() {
            Vec3d point = new Vec3d(1, 0.5, 1);
            assertTrue(area.contains(point));
        }

        @Test
        @DisplayName("Point on corner")
        public void testPointOnCorner() {
            Vec3d point = new Vec3d(0, 0, 0);
            assertTrue(area.contains(point));
        }

        @Test
        @DisplayName("Point on edge")
        public void testPointOnEdge() {
            Vec3d point = new Vec3d(2.5, 5, 2.5);
            assertTrue(area.contains(point));
        }

        @Test
        @DisplayName("Point outside")
        public void testPointOutside() {
            Vec3d point = new Vec3d(6, 8, 6);
            assertFalse(area.contains(point));
        }
    }

    @Nested
    @DisplayName("intersectsSync Tests")
    public class IntersectTests {
        @Test
        @DisplayName("Intersecting polygon")
        public void testIntersectingPolygons() {
            PolygonArea other = new PolygonArea();
            other.setPoints(Set.of(
                    new Vec3d(2.5, 2.5, 2.5),
                    new Vec3d(15, 5, 5),
                    new Vec3d(5, 5, 15),
                    new Vec3d(15, 5, 15),
                    new Vec3d(10, 15, 10)
            ));
            assertTrue(area.intersects(other));
        }

        @Test
        @DisplayName("Non-intersecting polygon")
        public void testNonIntersectingPolygons() {
            PolygonArea other = new PolygonArea();
            other.setPoints(Set.of(
                    new Vec3d(11, 11, 11),
                    new Vec3d(20, 11, 11),
                    new Vec3d(11, 11, 20),
                    new Vec3d(20, 11, 20),
                    new Vec3d(15, 15, 15)
            ));
            assertFalse(area.intersects(other));
        }

        @Test
        @DisplayName("Intersecting cuboid")
        public void testIntersectingCuboids() {
            CuboidArea other = new CuboidArea();
            other.setPoint(new Vec3d(5, 5, 5));
            other.setPoint(new Vec3d(15, 15, 15));
            assertTrue(area.intersects(other));
        }

        @Test
        @DisplayName("Non-intersecting cuboid")
        public void testNonIntersectingCuboids() {
            CuboidArea other = new CuboidArea();
            other.setPoint(new Vec3d(11, 11, 11));
            other.setPoint(new Vec3d(20, 20, 20));
            assertFalse(area.intersects(other));
        }
    }
}
