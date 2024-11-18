package de.dhbw.visualizer;

import de.dhbw.visualizer.mesh.MeshFactory;
import de.orat.math.view.euclidview3d.GeometryView3d;
import de.orat.math.xml.urdf.Visualizer;
import org.jogamp.vecmath.Matrix4d;

import java.io.File;

public class Main extends GeometryView3d {

    public static void main(String[] args) throws Exception {
        /*ViewerService viewerService = ViewerService.getInstance();
        var viewer = viewerService.getViewer().orElseThrow();
        viewer.open();

        viewer.addSphere(new Point3d(1, 1, 1), 3, Color.RED, "Test", false);
        viewer.addSphere(new Point3d(1, 1, 1), 4, Color.RED, "Test", true);
*/

        Visualizer visualizer = new Visualizer();

        var mesh = MeshFactory.loadDaeMesh(new File("111"));
      //  visualizer.getViewer().
        if (visualizer.getViewer() != null) {
            // Urdf jurdf = new Urdf(visualizer.getClass().getResourceAsStream("ur5eA.urdf"));
            //Chain chain = jurdf.createChain();

            visualizer.getViewer().open();
            visualizer.getViewer().addMesh("/data/objfiles/base.dae", new Matrix4d());

            //visualizer.add(chain);

        } else {
            System.out.println("No Visualizer implementation found!");
        }
    }
}
