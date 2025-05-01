package com.r3signed.ac.regions.internal.geometry;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.Vec3d;

public record Triangle(Vec3d a, Vec3d b, Vec3d c) {
    /**
     * Compute the faces of a polygon given a set of points.
     *
     * @param points The points of the polygon
     * @return A set of triangles representing the faces of the polygon
     */
    public static Set<Triangle> computeFaces(Set<Vec3d> points) {
        if (points.size() < 3) {
            return Set.of();
        }

        Vec3d[] pointArray = points.toArray(new Vec3d[0]);
        Set<Triangle> faces = new HashSet<>();

        for (int i = 0; i < pointArray.length - 2; i++) {
            for (int j = i + 1; j < pointArray.length - 1; j++) {
                for (int k = j + 1; k < pointArray.length; k++) {
                    faces.add(new Triangle(pointArray[i], pointArray[j], pointArray[k]));
                }
            }
        }

        return faces;
    }

    /**
     * Checks if a raycast intersects with the triangle.
     *
     * @param rayOrigin The origin of the raycast
     * @param rayDirection The direction of the raycast
     * @return True if the ray intersects with the triangle, otherwise false
     */
    public boolean rayIntersects(Vec3d rayOrigin, Vec3d rayDirection) {
        final double EPSILON = 0.000001;
        Vec3d edge1 = b.subtract(a);
        Vec3d edge2 = c.subtract(a);

        // Calculate the determinant
        Vec3d pVec = rayDirection.crossProduct(edge2);
        double det = edge1.dotProduct(pVec);

        // If near zero, the ray lies on the plane of the triangle
        if (det < EPSILON) {
            return false;
        }

        // Calculate U and test bounds
        double invDet = 1.0 / det;
        Vec3d tVec = rayOrigin.subtract(a);
        double u = invDet * tVec.dotProduct(pVec);
        if (u < 0.0 || u > 1.0) {
            return false;
        }

        // Calculate V and test bounds
        Vec3d qVec = tVec.crossProduct(edge1);
        double v = invDet * rayDirection.dotProduct(qVec);
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }

        // Intersection occurs if t is positive
        double t = invDet * edge2.dotProduct(qVec);
        return t > EPSILON;
    }
}
