package de.dhbw.visualizer.math;

import org.joml.Vector3d;

import java.util.Objects;

public record Sphere(Vector3d center, double radius) {

    public static int DISTANCE_COUNTER = 0;

    public double x() {
        return center.x();
    }

    public double y() {
        return center.y();
    }

    public double z() {
        return center.z();
    }

    public double distance(Sphere other) {
        double dx = this.x() - other.x();
        double dy = this.y() - other.y();
        double dz = this.z() - other.z();
        var distance = Math.sqrt(dx * dx + dy * dy + dz * dz) - (this.radius + other.radius);
        return Math.max(0, distance);
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
