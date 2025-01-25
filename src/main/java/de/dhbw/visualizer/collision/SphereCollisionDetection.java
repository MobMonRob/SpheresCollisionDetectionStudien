package de.dhbw.visualizer.collision;

import com.google.common.base.Preconditions;
import de.dhbw.visualizer.collision.approximation.SphereApproximationGenerator;
import de.dhbw.visualizer.collision.hierarchy.HierarchyTreeGenerator;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.tree.Node;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.visual.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SphereCollisionDetection {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Shape.ShapeType, SphereApproximationGenerator> approximation;
    private final HierarchyTreeGenerator hierarchyTreeGenerator;
    private final Map<Link, Node> boundingBoxRepresentations = new HashMap<>();

    private SphereCollisionDetection(Builder builder) {
        this.approximation = builder.approximation;
        this.hierarchyTreeGenerator = builder.hierarchyTreeGenerator;
    }

    private void setup(Chain chain) {
        for (var link : chain.getLinks().values()) {
            setupLink(link);
        }
    }

    private void setupLink(Link link) {
        var spheres = toSpheres(link);
        var tree = this.hierarchyTreeGenerator.constructHierarchyTree(spheres);

        boundingBoxRepresentations.put(link, tree);
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
        return spheres;
    }

    public static class Builder {

        private final Map<Shape.ShapeType, SphereApproximationGenerator> approximation = new HashMap<>();
        private Chain chain;
        private HierarchyTreeGenerator hierarchyTreeGenerator;

        private Builder() {
        }

        public Builder approximation(SphereApproximationGenerator approximationGenerator) {
            this.approximation.put(approximationGenerator.getShapeType(), approximationGenerator);
            return this;
        }

        public Builder hierarchyTree(HierarchyTreeGenerator hierarchyTreeGenerator) {
            this.hierarchyTreeGenerator = hierarchyTreeGenerator;
            return this;
        }

        public Builder setRobotChain(Chain chain) {
            this.chain = chain;
            return this;
        }

        public SphereCollisionDetection build() {
            assert this.hierarchyTreeGenerator != null;
            assert this.chain != null;

            var collisionDetection = new SphereCollisionDetection(this);
            collisionDetection.setup(this.chain);
            return collisionDetection;
        }
    }
}
