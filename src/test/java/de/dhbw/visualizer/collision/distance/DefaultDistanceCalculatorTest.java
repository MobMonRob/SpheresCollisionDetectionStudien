package de.dhbw.visualizer.collision.distance;

import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.tree.Node;
import org.joml.Vector3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultDistanceCalculatorTest {

    @Test
    void testExactDistanceBetweenTwoSpheres() {
        var node1 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var node2 = Node.leafNode(new Sphere(new Vector3d(15, 0, 0), 5));
        var calculator = new DefaultDistanceCalculator(0.0);// alpha of zero for exact calculation

        double distance = calculator.distance(node1, node2);
        Assertions.assertEquals(5.0, distance, 0.001);
    }

    @Test
    void testOverlappingSpheres() {
        var node1 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var node2 = Node.leafNode(new Sphere(new Vector3d(8, 0, 0), 5));
        var calculator = new DefaultDistanceCalculator(0.0);

        double distance = calculator.distance(node1, node2);
        Assertions.assertEquals(0.0, distance, 0.001, "Overlapping spheres should have a distance of 0.");
    }

    @Test
    void testDistanceWithAlphaApproximation() {
        var node1 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var node2 = Node.leafNode(new Sphere(new Vector3d(15, 0, 0), 5));
        var calculator = new DefaultDistanceCalculator(0.1);

        double distance = calculator.distance(node1, node2);
        Assertions.assertTrue(distance <= 5.0, "The distance should be slightly underestimated due to error tolerance.");
    }

    @Test
    void testIdenticalSpheres() {
        var node1 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var node2 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var calculator = new DefaultDistanceCalculator(0);

        double distance = calculator.distance(node1, node2);
        Assertions.assertEquals(0.0, distance, 0.001, "Identical spheres should have a distance of 0.");
    }

    @Test
    void testDistantSpheres() {
        var node1 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 5));
        var node2 = Node.leafNode(new Sphere(new Vector3d(50, 0, 0), 5));
        var calculator = new DefaultDistanceCalculator(0);

        double distance = calculator.distance(node1, node2);
        Assertions.assertEquals(40.0, distance, 0.001, "The calculated distance should be 40.");
    }

    @Test
    void testHierarchicalBoundingTree() {
        // Creating a hierarchical tree structure
        var child1 = Node.leafNode(new Sphere(new Vector3d(5, 5, 5), 3));
        var child2 = Node.leafNode(new Sphere(new Vector3d(15, 5, 5), 3));

        var node1 = new Node(new Sphere(new Vector3d(10, 5, 5), 6), child1, child2);
        var node2 = Node.leafNode(new Sphere(new Vector3d(25, 5, 5), 3));
        var calculator = new DefaultDistanceCalculator(0);

        double distance = calculator.distance(node1, node2);

        Assertions.assertTrue(distance > 0, "The distance should be positive as the objects do not overlap.");
    }

    @Test
    void testComplexMultiLevelHierarchy() {
        // Constructing a complex hierarchical bounding structure
        var leaf1 = Node.leafNode(new Sphere(new Vector3d(-10, 0, 0), 2));
        var leaf2 = Node.leafNode(new Sphere(new Vector3d(0, 0, 0), 2));
        var leaf3 = Node.leafNode(new Sphere(new Vector3d(10, 0, 0), 2));

        var internal1 = new Node(new Sphere(new Vector3d(-5, 0, 0), 4), leaf1, leaf2);
        var internal2 = new Node(new Sphere(new Vector3d(5, 0, 0), 4), leaf3, null);

        var root = new Node(new Sphere(new Vector3d(0, 0, 0), 8), internal1, internal2);
        var testSphere = Node.leafNode(new Sphere(new Vector3d(20, 0, 0), 2));

        var calculator = new DefaultDistanceCalculator(0.05); // 5% tolerance
        double distance = calculator.distance(root, testSphere);

        Assertions.assertTrue(distance > 0, "Distance should be positive since the objects do not overlap.");
    }
}
