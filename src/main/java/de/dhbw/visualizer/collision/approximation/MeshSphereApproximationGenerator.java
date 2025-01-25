package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.orat.math.xml.urdf.visual.Mesh;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.List;

// TODO: "Scan conversation"
public class MeshSphereApproximationGenerator implements SphereApproximationGenerator {

    @Override
    public List<Sphere> toSphere(Shape inputData) {
        if (!(inputData instanceof Mesh mesh)) {
            throw new IllegalArgumentException("Invalid input type " + inputData.getClass().getName());
        }

        return List.of();
    }

    @Override
    public Shape.ShapeType getShapeType() {
        return Shape.ShapeType.mesh;
    }
}
