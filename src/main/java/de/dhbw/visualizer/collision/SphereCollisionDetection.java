package de.dhbw.visualizer.collision;

import com.google.common.base.Preconditions;
import de.dhbw.visualizer.Stl;
import de.dhbw.visualizer.collision.approximation.SphereGenerator;
import de.dhbw.visualizer.collision.distance.DistanceCalculator;
import de.dhbw.visualizer.collision.hierarchy.HierarchyTreeGenerator;
import de.dhbw.visualizer.jurdf.ProcessedChain;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.tree.Node;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.visual.Shape;
import lombok.Getter;

import java.util.*;
import java.util.logging.Logger;

public final class SphereCollisionDetection {

    /**
     * Used to visualize the different links.
     *
     * @deprecated Should be replaced with a more robust system, which should also work with other formats than stl.
     */
    @Deprecated(since = "1.0.0")
    public static final Map<Stl, CollisionParameters> VISUALIZATION = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(SphereCollisionDetection.class.getSimpleName());

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Shape.ShapeType, SphereGenerator> approximation;
    private final HierarchyTreeGenerator hierarchyTreeGenerator;
    private final DistanceCalculator distanceCalculator;

    private final Map<String, Node> boundingBoxRepresentations = new HashMap<>();
    @Getter
    private final Map<String, List<Sphere>> rawSpheres = new HashMap<>();
    private final Chain chain;

    private SphereCollisionDetection(Builder builder) {
        this.approximation = builder.approximation;
        this.distanceCalculator = builder.distanceCalculator;
        this.hierarchyTreeGenerator = builder.hierarchyTreeGenerator;
        this.chain = builder.chain;
    }

    private void setup() {
        var helper = ProcessedChain.chain(this.chain);

        for (var link : helper.getLinks()) {
            setupLink(link);
        }
    }

    private void setupLink(Link link) {
        var spheres = toSpheres(link);
        this.rawSpheres.put(link.getName(), spheres);
        var tree = this.hierarchyTreeGenerator.constructHierarchyTree(spheres);

        boundingBoxRepresentations.put(link.getName(), tree);
    }

    private List<Sphere> toSpheres(Link link) {
        List<Sphere> spheres = new ArrayList<>();

        for (CollisionParameters collisionParameter : link.getCollisionParameters()) {
            spheres.addAll(toSpheres(collisionParameter));
        }

        LOGGER.info(() -> link.getName() + " spheres: " + spheres.size());
        return spheres;
    }

    private List<Sphere> toSpheres(CollisionParameters collisionParameter) {
        var shape = collisionParameter.getShape();
        var approximator = this.approximation.get(shape.getShapeType());

        if (approximator == null) {
            throw new IllegalArgumentException(String.format("No shape approximator defined for %s", shape.getShapeType()));
        }

        return approximator.toSphere(shape, collisionParameter);
    }

    public void calculateStats() {
        LOGGER.info("---- STATS ----");
        for (Map.Entry<String, Node> entry : boundingBoxRepresentations.entrySet()) {
            int spheres = entry.getValue() == null ? 0 : entry.getValue().size();
            LOGGER.info(() -> String.format("%s: %d sphere(s)", entry.getKey(), spheres));
        }

        LOGGER.info(() -> String.format("  TOTAL: %d link(s) with a total of %d sphere(s)",
                boundingBoxRepresentations.size(),
                boundingBoxRepresentations.values().stream()
                        .filter(Objects::nonNull)
                        .mapToInt(Node::size).sum()
        ));
    }

    /**
     * Checks if the robot collides with itself.
     *
     * @return A {@link SelfCollisionResult} containing a list of colliding links. Status flag is OK if no collision was detected.
     */
    public SelfCollisionResult detectSelfCollision() {
        List<SelfCollisionResult.CollisionEntry> collidedLinks = new ArrayList<>();

        // Iterate through all link combinations
        for (Link first : chain.getLinks().values()) {
            for (Link second : chain.getLinks().values()) {
                if (first.getName().equals(second.getName()) || first.getParentLink() != null && second.getName().equals(first.getParentLink().getName()) ||
                        first.getChildrenLinks().stream().anyMatch(l -> l.getName().equals(second.getName()))) {
                    continue;
                }

                var collision = collide(boundingBoxRepresentations.get(first.getName()), boundingBoxRepresentations.get(second.getName()));

                if (collision) {
                    collidedLinks.add(new SelfCollisionResult.CollisionEntry(first.getName(), second.getName()));
                }
            }
        }
        /*for (Map.Entry<String, Node> e1 : this.boundingBoxRepresentations.entrySet()) {
            for (Map.Entry<String, Node> e2 : this.boundingBoxRepresentations.entrySet()) {
                System.out.println("L1: " + e1.getKey() + " " + e2.getKey() + " " + this.chain.getLink(e1.getKey()).getParentLink().getName());
                if (e1.getKey().equals(e2.getKey())) {
                    continue;
                }

                var collision = collide(e1.getValue(), e2.getValue());

                if (collision) {
                    collidedLinks.add(new SelfCollisionResult.CollisionEntry(e1.getKey(), e2.getKey()));
                }
            }
        }*/

        return SelfCollisionResult.fromStatusList(collidedLinks);
    }

    /**
     * Checks if two hierarchy trees collide.
     */
    private boolean collide(Node n1, Node n2) {
        if (n1 == null || n2 == null) {
            return false;
        }

        return this.distanceCalculator.distance(n1, n2) <= 0;
    }

    public static class Builder {

        private final Map<Shape.ShapeType, SphereGenerator> approximation = new EnumMap<>(Shape.ShapeType.class);
        private Chain chain;
        private HierarchyTreeGenerator hierarchyTreeGenerator;
        private DistanceCalculator distanceCalculator;

        private Builder() {
        }

        public Builder approximation(SphereGenerator approximationGenerator) {
            this.approximation.put(approximationGenerator.getShapeType(), approximationGenerator);
            return this;
        }

        public Builder hierarchyTree(HierarchyTreeGenerator hierarchyTreeGenerator) {
            this.hierarchyTreeGenerator = hierarchyTreeGenerator;
            return this;
        }

        public Builder distanceCalculator(DistanceCalculator distanceCalculator) {
            this.distanceCalculator = distanceCalculator;
            return this;
        }

        public Builder setRobotChain(Chain chain) {
            this.chain = chain;
            return this;
        }

        public SphereCollisionDetection build() {
            Preconditions.checkNotNull(this.hierarchyTreeGenerator, "HierarchyTreeGenerator can't be null.");
            Preconditions.checkNotNull(this.chain, "URDF Chain can't be null.");
            Preconditions.checkNotNull(this.distanceCalculator, "Distance calculator can't be null.");

            var collisionDetection = new SphereCollisionDetection(this);
            collisionDetection.setup();
            return collisionDetection;
        }
    }
}
