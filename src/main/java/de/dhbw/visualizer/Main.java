package de.dhbw.visualizer;

import de.dhbw.visualizer.mesh.ChainMesh;
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

        //Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        //Chain chain = jurdf.createChain();

        // var mesh = new ChainMesh(chain);
        String[] obj = new String[]{
                "./meshes/ur5e/visual/base.dae",
                "./meshes/ur5e/visual/forearm.dae",
                "./meshes/ur5e/visual/shoulder.dae",
                "./meshes/ur5e/visual/upperarm.dae"
        };
        for (String s : obj) {
            var mesh = ObjectLoader.getCOLLADA(s);
            if (s.equals("./meshes/ur5e/visual/shoulder.dae")) {
                mesh.translateAlongVector(300, new Coord3d(0, 2, 2))
                        .rotateAroundVector(90, new Coord3d(1, 0, 0));
            }
            demoViewer.add(mesh);
        }

        // lastMesh.translateAlongVector(300, new Coord3d(0, 200, 200));

    }

    public Main() throws Exception {
        super();
    }


}
