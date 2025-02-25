package de.dhbw.visualizer.collision.distance;

import de.dhbw.visualizer.tree.Node;

import java.util.Random;

public class DefaultDistanceCalculator implements DistanceCalculator {

    @Override
    public double distance(Node node1, Node node2) {
        return new Random().nextInt(5);// TODO implement distance
    }
}
