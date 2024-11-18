package de.dhbw.visualizer;

import java.util.List;

/**
 * Represents a collection of spheres used to approximate a concrete object.
 */
public class SphereGroup {

    private final List<Sphere> spheres;

    public SphereGroup(List<Sphere> spheres) {
        this.spheres = spheres;
    }
}
