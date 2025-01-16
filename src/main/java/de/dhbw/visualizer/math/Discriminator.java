package de.dhbw.visualizer.math;

public record Discriminator(
        Axis axis,
        double value
) {

    public static Discriminator createWithCenterValue(Axis axis, double value) {
        return new Discriminator(axis, value / 2);
    }
}
