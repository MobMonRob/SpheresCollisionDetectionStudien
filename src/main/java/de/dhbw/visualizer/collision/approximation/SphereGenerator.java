package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.math.Sphere;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.List;

public interface SphereGenerator {

    /**
     * @param shape Die eingegebene Form.
     * @param link Der eingegebene Kollisionsparameter des URDF-Links
     * @return Eine Liste von Kugeln, die die Form abbilden
     * @throws UnsupportedOperationException - Wenn die eingegebene Form nicht von diesem SphereGenerator unterstützt wird.
     */
    List<Sphere> toSphere(String name,Shape shape, CollisionParameters link);

    Shape.ShapeType getShapeType();
}
