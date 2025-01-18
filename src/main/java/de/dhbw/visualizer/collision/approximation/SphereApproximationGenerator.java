package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;

import java.util.List;

public interface SphereApproximationGenerator<T> {

    List<Sphere> toSphere(T inputData);

    Class<T> getInputClass();
}
