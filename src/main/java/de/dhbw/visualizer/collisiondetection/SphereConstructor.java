package de.dhbw.visualizer.collisiondetection;

import org.jogamp.vecmath.Point3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @see "Efficient Collision and Self-Collision Detection for Humanoids Based on Sphere Trees Hierarchies"
 */
public class SphereConstructor {

    public void compactSphereConstruction(List<Point3d> V) {
        List<Point3d> U = new ArrayList<>();
        var v1 = V.get(0);
        var v2 = V.get(1);
        U.add(v1);
        U.add(v2);

        var c = center(v1, v2);
        var r = euclideanDistance(c, v1);


        while (!V.isEmpty()) {
            var v = V.get(0);
            V.remove(0);

            if (euclideanDistance(v, c) > r) {
                double max = 0;//TODO
                c = update(c, v, max);
                r = euclideanDistance(c, v);
            }

            U.add(v);
        }
    }

    public Point3d update(Point3d c, Point3d v, double max) {
        return c;//TODO
    }

    public double euclideanDistance(Point3d p1, Point3d p2) {
        return Math.sqrt(
                (p1.x - p2.x) * (p1.x - p2.x)
                        + (p1.y - p2.y) * (p1.y - p2.y)
                        + (p1.z - p2.z) * (p1.z - p2.z)
        );
    }

    public Point3d center(Point3d p1, Point3d p2) {
        return new Point3d(0.5 * (p1.x + p2.x),
                0.5 * (p1.y + p2.y),
                0.5 * (p1.z + p2.z));
    }
}
