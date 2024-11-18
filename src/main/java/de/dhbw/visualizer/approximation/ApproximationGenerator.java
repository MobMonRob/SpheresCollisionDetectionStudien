package de.dhbw.visualizer.approximation;

import de.dhbw.visualizer.SphereGroup;

public interface ApproximationGenerator<T> {

    /**
     * Approximates a sphere group of a given object.
     *
     * @param object The shape that should be approximated.
     * @return The resulting sphere structure.
     */
    SphereGroup approximate(T object);
}
