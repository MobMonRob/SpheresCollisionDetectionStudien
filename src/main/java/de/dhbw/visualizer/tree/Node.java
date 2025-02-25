package de.dhbw.visualizer.tree;

import de.dhbw.visualizer.math.Sphere;

public record Node(
        Sphere sphere,
        Node left,
        Node right
) {

    public static Node leafNode(Sphere sphere) {
        return new Node(sphere, null, null);
    }

    /**
     * A node is considered a leaf if it has no child elements.
     *
     * @return If the element is a leaf.
     */
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

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
