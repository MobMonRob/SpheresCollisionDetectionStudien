package de.dhbw.visualizer;

import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.collision.approximation.MeshSphereApproximationGenerator;
import de.dhbw.visualizer.collision.approximation.SphereForwardApproximationGenerator;
import de.dhbw.visualizer.collision.hierarchy.DivideAndConquerHierarchyTreeGenerator;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Urdf;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();
        var collisionDetection = SphereCollisionDetection.builder()
                .approximation(new SphereForwardApproximationGenerator())
                .approximation(new MeshSphereApproximationGenerator())
                .hierarchyTree(new DivideAndConquerHierarchyTreeGenerator())
                .setRobotChain(chain)
                .build();
        System.out.println(collisionDetection);
    }
}
