package de.dhbw.visualizer.collision.hierarchy;

import de.dhbw.visualizer.math.Discriminator;
import de.dhbw.visualizer.math.RectangularBB;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.tree.Node;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DivideAndConquerHierarchyTreeGenerator implements HierarchyTreeGenerator {

    @Override
    public Node constructHierarchyTree(List<Sphere> spheres) {
        // If there are several points in the same place, they prevent the algorithm from constructing the tree correctly.
        // For this reason, all duplicates are removed at the beginning
        var sphereSet = new HashSet<>(spheres);
        return divide(new ArrayList<>(sphereSet));
    }

    private Node divide(List<Sphere> spheres) {
        // Smallest possible node.
        if (spheres.isEmpty()) {
            return null;
        }

        if (spheres.size() == 2) {
            return new Node(
                    calculateCompleteSphereFromChildren(spheres.get(0), spheres.get(1)),
                    new Node(spheres.get(0), null, null),
                    new Node(spheres.get(0), null, null)
            );
        }

        if (spheres.size() == 1) {
            return new Node(
                    spheres.get(0), null, null
            );
        }


        return divideSphereList(spheres);
    }

    private Node divideSphereList(List<Sphere> spheres) {
        var rectangular = constructRectangularBB(spheres);
        var discriminator = rectangular.findDiscriminator();

        List<Sphere> upper = new ArrayList<>();
        List<Sphere> lower = new ArrayList<>();

        // Split spheres into smaller and bigger
        for (Sphere sphere : spheres) {
            var side = decideSide(sphere, discriminator);

            if (side == Side.UPPER) {
                upper.add(sphere);
            } else if (side == Side.LOWER) {
                lower.add(sphere);
            }
        }

        if (upper.size() == spheres.size() || lower.size() == spheres.size()) {
            throw new IllegalArgumentException(String.format("Invalid size! (u: %d / l: %d / c: %d)", upper.size(), lower.size(), spheres.size()));
        }

        var upperNode = divide(upper);
        var lowerNode = divide(lower);
        Sphere complete = calculateCompleteSphere(upperNode, lowerNode);

        return new Node(
                complete,
                upperNode,
                lowerNode
        );
    }

    private Sphere calculateCompleteSphere(Node upper, Node lower) {
        /*
        TODO The second method
        directly considers the leaf spheres. We first select a
        center for the bounding sphere and then examine each of
        the descendant leaf spheres to determine the minimum
        radius required. The selection of the bounding sphere
        center is done by using the average position of the centers
        of the leaf spheres, which has already been calculated in
        the process of dividing the leaf spheres. The first method
        works well near the leaves of the tree, while the second
        methods produces better results closer to the root.
         */
        return calculateCompleteSphereFromChildren(upper.sphere(), lower.sphere());
    }

    // Trivial algorithm
    private Sphere calculateCompleteSphereFromChildren(Sphere upper, Sphere lower) {
        var center = new Vector3d((upper.x() + lower.x()) / 2, (upper.y() + lower.y()) / 2, (upper.z() + lower.z()) / 2);
        var difference = new Vector3d(upper.center()).sub(lower.center());
        double distance = Math.sqrt(difference.x() * difference.x() + difference.y() * difference.y() + difference.z() * difference.z());
        double newRadius = (distance + upper.radius() + lower.radius()) / 2;

        // Radius must be a number
        assert !Double.isNaN(newRadius);

        return new Sphere(center, newRadius);
    }

    private Side decideSide(Sphere sphere, Discriminator discriminator) {
        var axis = discriminator.axis();
        var value = discriminator.value();

        return switch (axis) {
            case X -> decideSide(sphere.center().x(), value);
            case Y -> decideSide(sphere.center().y(), value);
            case Z -> decideSide(sphere.center().z(), value);
        };
    }

    private Side decideSide(double value, double discriminator) {
        return discriminator > value ? Side.UPPER : Side.LOWER;
    }

    private RectangularBB constructRectangularBB(List<Sphere> spheres) {
        double minX = 10000000;
        double minY = 10000000;
        double minZ = 10000000;

        double maxX = -10000000; // extremely small number
        double maxY = -10000000;
        double maxZ = -10000000;

        for (Sphere sphere : spheres) {
            minX = Math.min(minX, sphere.center().x());
            minY = Math.min(minY, sphere.center().y());
            minZ = Math.min(minZ, sphere.center().z());

            maxX = max(maxX, sphere.center().x());
            maxY = max(maxY, sphere.center().y());
            maxZ = max(maxZ, sphere.center().z());
        }

        return new RectangularBB(
                new Vector3d(minX, minY, minZ),
                new Vector3d(maxX, maxY, maxZ)
        );
    }

    private enum Side {
        LOWER,
        UPPER
    }

    private static double max(double v1, double v2) {
        return v1 > v2 ? v1 : v2;
    }
}
