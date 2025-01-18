package de.dhbw.visualizer.math;

import org.joml.Vector3d;

import java.util.Objects;

public record Sphere(Vector3d center, double radius) {

    public double x() {
        return center.x();
    }

    public double y() {
        return center.y();
    }

    public double z() {
        return center.z();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sphere sphere = (Sphere) o;
        return Objects.equals(center, sphere.center);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(center);
    }
}
