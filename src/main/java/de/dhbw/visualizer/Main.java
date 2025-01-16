package de.dhbw.visualizer;

import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.collision.approximation.TriangleApproximationGenerator;
import de.dhbw.visualizer.collision.hierarchy.DivideAndConquerHierarchyTreeGenerator;
import de.dhbw.visualizer.object.DrawableChainLoader;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Urdf;

public class Main extends Visualizer {

    public static void main(String[] args) throws Exception {
        var collisionDetection = SphereCollisionDetection.builder()
                .approximation(new TriangleApproximationGenerator())
                .hierarchyTree(new DivideAndConquerHierarchyTreeGenerator())
                .build();

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        DemoViewer demoViewer = DemoViewer.initAndOpen();

        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();

        // var mesh = new ChainMesh(chain);
        DrawableChainLoader.loadAndTestBaseModel(demoViewer.getChart());
        //TODO Production DrawableChainLoader.load(demoViewer.getChart(), chain);
        // lastMesh.translateAlongVector(300, new Coord3d(0, 200, 200));
    }

    public Main() throws Exception {
        super();
    }


}
