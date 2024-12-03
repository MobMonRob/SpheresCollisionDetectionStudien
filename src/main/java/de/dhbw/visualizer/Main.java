package de.dhbw.visualizer;

import de.dhbw.visualizer.mesh.ChainMesh;
import de.dhbw.visualizer.object.DrawableChainLoader;
import de.dhbw.visualizer.object.ObjectLoader;
import de.dhbw.visualizer.object.mesh.MeshPart;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Urdf;
import org.jzy3d.maths.Coord3d;

public class Main extends Visualizer {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        DemoViewer demoViewer = DemoViewer.initAndOpen();

        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();

        // var mesh = new ChainMesh(chain);

        demoViewer.add(DrawableChainLoader.load(chain));
        // lastMesh.translateAlongVector(300, new Coord3d(0, 200, 200));

    }

    public Main() throws Exception {
        super();
    }


}
