package de.dhbw.visualizer.collision.distance;

import de.dhbw.visualizer.tree.Node;

public class DefaultDistanceCalculator implements DistanceCalculator {

    private final double alpha;

    public DefaultDistanceCalculator(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double distance(Node node1, Node node2) {
        return search(node1, node2, 100000);
    }

    private double search(Node node1, Node node2, double d) {
        if (node1 == null || node2 == null) {
            return d;
        }

        double distance = node1.sphere().distance(node2.sphere());

        // Return if distance between n1 and n2 is wider than the current minimum
        if (distance >= d) {
            return d;
        }

        if (node1.isLeaf() || node2.isLeaf()) {
            return Math.min(d, distance * (1 - this.alpha));
        }

        var splitNode = findSplitNode(node1, node2);
        var otherNode = (splitNode == node2) ? node1 : node2;

        double newDistance = search(splitNode.left(), otherNode, d);
        return search(splitNode.left(), otherNode, newDistance);
    }

    private Node findSplitNode(Node node1, Node node2) {
        if (node1.isLeaf()) {
            return node1;
        }

        if (node2.isLeaf()) {
            return node2;
        }

        return (node1.sphere().radius() > node2.sphere().radius()) ? node1 : node2;
    }
}
