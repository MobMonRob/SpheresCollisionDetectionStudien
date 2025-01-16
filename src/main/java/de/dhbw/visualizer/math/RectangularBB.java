package de.dhbw.visualizer.math;

import org.joml.Vector3d;

public record RectangularBB(
        Vector3d min,
        Vector3d max
) {
    public Vector3d length() {
        return new Vector3d(max).sub(min);
    }

    /**
     * @return The axes along which the bounding box is longest.
     */
    public Discriminator findDiscriminator() {
        var length = length();
        var x = length.x();
        var y = length.y();
        var z = length.z();

        if (x >= y && x >= z) {
            return new Discriminator(Axis.X, (min.x() + max.x()) / 2);
        }

        if (y >= x && y >= z) {
            return new Discriminator(Axis.Y, (min.y() + max.y()) / 2);
        }

        return new Discriminator(Axis.Z, (min.z() + max.z()) / 2);
    }
}