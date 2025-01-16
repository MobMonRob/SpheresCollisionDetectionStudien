package de.dhbw.visualizer.collision;

import de.dhbw.visualizer.collision.approximation.SphereApproximationGenerator;
import de.dhbw.visualizer.collision.hierarchy.HierarchyTreeGenerator;

import java.util.HashMap;
import java.util.Map;

public class SphereCollisionDetection {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Class<?>, SphereApproximationGenerator<?>> approximation;
    private final HierarchyTreeGenerator hierarchyTreeGenerator;

    private SphereCollisionDetection(Builder builder) {
        this.approximation = builder.approximation;
        this.hierarchyTreeGenerator = builder.hierarchyTreeGenerator;
    }

    public static class Builder {

        private final Map<Class<?>, SphereApproximationGenerator<?>> approximation = new HashMap<>();
        private HierarchyTreeGenerator hierarchyTreeGenerator;

        private Builder() {
        }

        public <T> Builder approximation(SphereApproximationGenerator<T> approximationGenerator) {
            this.approximation.put(approximationGenerator.getInputClass(), approximationGenerator);
            return this;
        }

        public Builder hierarchyTree(HierarchyTreeGenerator hierarchyTreeGenerator) {
            this.hierarchyTreeGenerator = hierarchyTreeGenerator;
            return this;
        }

        public SphereCollisionDetection build() {
            return new SphereCollisionDetection(this);
        }
    }
}
