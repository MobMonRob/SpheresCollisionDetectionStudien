package de.dhbw.visualizer.collision;

import com.google.common.base.Preconditions;
import de.dhbw.visualizer.collision.approximation.SphereGenerator;
import de.dhbw.visualizer.collision.distance.DistanceCalculator;
import de.dhbw.visualizer.collision.hierarchy.HierarchyTreeGenerator;
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

    private SphereCollisionDetection(Builder builder) {
        this.approximation = builder.approximation;
        this.distanceCalculator = builder.distanceCalculator;
        this.hierarchyTreeGenerator = builder.hierarchyTreeGenerator;
    }

    private void setup(Chain chain) {
        for (var link : chain.getLinks().values()) {
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
            var shape = collisionParameter.getShape();
            var approximator = this.approximation.get(shape.getShapeType());

            if (approximator == null) {
                throw new IllegalArgumentException(String.format("No shape approximator defined for %s", shape.getShapeType()));
            }

            var shapeSpheres = approximator.toSphere(shape);
            spheres.addAll(shapeSpheres);
        }
        LOGGER.info(link.getName() + " spheres: " + spheres.size());
        return spheres;
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
        for (Map.Entry<String, Node> e1 : this.boundingBoxRepresentations.entrySet()) {
            for (Map.Entry<String, Node> e2 : this.boundingBoxRepresentations.entrySet()) {
                if (e1.getKey().equals(e2.getKey())) {
                    continue;
                }

                var collision = collide(e1.getValue(), e2.getValue());

                if (collision) {
                    collidedLinks.add(new SelfCollisionResult.CollisionEntry(e1.getKey(), e2.getKey()));
                }
            }
        }

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

        private final Map<Shape.ShapeType, SphereGenerator> approximation = new HashMap<>();
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
            collisionDetection.setup(this.chain);
            return collisionDetection;
        }
    }
}
