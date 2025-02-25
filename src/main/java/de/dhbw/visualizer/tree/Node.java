package de.dhbw.visualizer.tree;

import de.dhbw.visualizer.math.Sphere;

public record Node(
        Sphere sphere,
        Node left,
        Node right
) {

    public int size() {
        int size = 0;

        if (this.sphere != null) {
            size++;
        }

        if (this.left != null) {
            size += this.left.size();
        }

        if (this.right != null) {
            size += this.right.size();
        }

        return size;
    }
}
