package de.dhbw.visualizer.collision.hierarchy;

import de.dhbw.visualizer.Sphere;
import de.dhbw.visualizer.tree.Node;

import java.util.List;

public interface HierarchyTreeGenerator {

    Node constructHierarchyTree(List<Sphere> spheres);
}
