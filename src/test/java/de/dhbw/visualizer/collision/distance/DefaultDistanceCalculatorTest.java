package de.dhbw.visualizer.collision.distance;

import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.tree.Node;
import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultDistanceCalculatorTest {

    @Test
    void testDistanceCalculation() {
        var node1 = new Node(new Sphere(new Vector3d(0, 0, 0), 1), null, null);
        var node2 = new Node(new Sphere(new Vector3d(2, 0, 0), 1), null, null);

        var calculator = new DefaultDistanceCalculator();
        Assertions.assertEquals(1, calculator.distance(node1, node2));
    }
}
