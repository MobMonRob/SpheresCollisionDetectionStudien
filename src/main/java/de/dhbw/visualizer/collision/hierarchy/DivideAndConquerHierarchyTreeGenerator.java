package de.dhbw.visualizer.collision.hierarchy;

import de.dhbw.visualizer.Sphere;
import de.dhbw.visualizer.math.Discriminator;
import de.dhbw.visualizer.math.RectangularBB;
import de.dhbw.visualizer.tree.Node;
import org.joml.Vector3d;

import java.util.*;

public class DivideAndConquerHierarchyTreeGenerator implements HierarchyTreeGenerator {

    public static void main(String[] args) {
        var treeGenerator = new DivideAndConquerHierarchyTreeGenerator();

        for (int count = 0; count < 100; count++) {
            List<Sphere> spheres = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < 10_00000; i++) {
                spheres.add(new Sphere(new Vector3d(random.nextInt(0, 100), random.nextInt(0, 100), random.nextInt(0, 100)),
                        random.nextInt(1, 5)));
            }

            long time = System.currentTimeMillis();
            // Random number generator can generate multiple points with same center position.
            // These duplicates are removed, to prevent a stack overflow exception
            var sphereSet = new HashSet<>(spheres);
            var tree = treeGenerator.constructHierarchyTree(new ArrayList<>(sphereSet));
            time = System.currentTimeMillis() - time;
            System.out.printf("Tree generated in %dms%n", time);
        }
    }

    @Override
    public Node constructHierarchyTree(List<Sphere> spheres) {
        return divide(spheres);
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
        There is no obvious way to compute the smallest
        sphere that contains a set of spheres, so we use two
        heuristic methods and select the smaller of the two
        spheres. The first method finds a bounding sphere that
        contains the spheres of the two children nodes and hence,
        by induction, all the descendant leaf nodes. The position
        and size of such a sphere can be determined optimally and
        uniquely as we are only bounding two spheres; the details
        are trivial and not included here. The second method
        directly considers the leaf spheres. We first select a
        center for the bounding sphere and then examine each of
        the descendant leaf spheres to determine the minimum
        radius required. The selection of the bounding sphere
        center is done by using the average position of the centers
        of the leaf spheres, which has already been calculated in
        the process of dividing the leaf spheres. The first method
        works well near the leaves of the tree, while the second
        methods produces better results closer to the root.
        TODO implement the other described way
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
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;

        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (Sphere sphere : spheres) {
            minX = Math.min(minX, sphere.center().x());
            minY = Math.min(minY, sphere.center().y());
            minZ = Math.min(minZ, sphere.center().z());

            maxX = Math.max(maxX, sphere.center().x());
            maxY = Math.max(maxY, sphere.center().y());
            maxZ = Math.max(maxZ, sphere.center().z());
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
}
