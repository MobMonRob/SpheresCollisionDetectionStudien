package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.Triangle;

import java.util.List;

public class TriangleApproximationGenerator implements SphereApproximationGenerator<Triangle> {

    @Override
    public List<Sphere> toSphere(Triangle inputData) {
        return List.of();
    }

    @Override
    public Class<Triangle> getInputClass() {
        return Triangle.class;
    }
}
