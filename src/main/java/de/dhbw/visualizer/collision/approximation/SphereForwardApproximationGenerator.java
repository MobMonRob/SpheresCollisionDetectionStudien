package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.List;

public class SphereForwardApproximationGenerator implements SphereApproximationGenerator {
    @Override
    public List<Sphere> toSphere(Shape inputData) {
        if (!(inputData instanceof de.orat.math.xml.urdf.visual.Sphere urdfSphere)) {
            throw new IllegalArgumentException("Invalid type " + inputData.getClass().getName());
        }
        //TODO parse sphere
        return List.of();
    }

    @Override
    public Shape.ShapeType getShapeType() {
        return Shape.ShapeType.sphere;
    }
}
