package de.dhbw.visualizer.math;

import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SphereTest {

    @Test
    void testDistance() {
        var s1 = new Sphere(new Vector3d(0, 0, 0), 1);
        var s2 = new Sphere(new Vector3d(3, 0, 0), 1);

        Assertions.assertEquals(1, s1.distance(s2));
    }

    @Test
    void testDistance2() {
        var s1 = new Sphere(new Vector3d(0, 0, 0), 1.5);
        var s2 = new Sphere(new Vector3d(3, 0, 0), 1);

        Assertions.assertEquals(0.5, s1.distance(s2));
    }

    @Test
    void testDistance3() {
        var s1 = new Sphere(new Vector3d(3, 0, 0), 1);
        var s2 = new Sphere(new Vector3d(3, 0, 0), 1);

        Assertions.assertEquals(0, s1.distance(s2));
    }
}
