package de.dhbw.visualizer.tree;

import de.dhbw.visualizer.math.Sphere;

public record Node(
        Sphere sphere,
        Node left,
        Node right
) {
}
