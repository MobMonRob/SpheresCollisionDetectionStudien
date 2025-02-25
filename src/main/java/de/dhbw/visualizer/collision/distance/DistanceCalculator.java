package de.dhbw.visualizer.collision.distance;

import de.dhbw.visualizer.tree.Node;

public interface DistanceCalculator {

    /**
     * Calculates the distance between two nodes.
     *
     * @param node1 The first node
     * @param node2 The second node
     * @return The minimal distance between two sphere hierarchy trees. 0 if the objects collide.
     */
    double distance(Node node1, Node node2);
}
