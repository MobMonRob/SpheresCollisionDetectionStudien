package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.List;

public interface SphereGenerator {

    List<Sphere> toSphere(Shape inputData, CollisionParameters link);

    Shape.ShapeType getShapeType();
}
