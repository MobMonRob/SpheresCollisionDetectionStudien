package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.List;

public interface SphereGenerator {

    List<Sphere> toSphere(Shape inputData);

    Shape.ShapeType getShapeType();
}
