package de.dhbw.visualizer;

import de.orat.math.view.euclidview3d.GeometryView3d;
import de.orat.view3d.euclid3dviewapi.api.ViewerService;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

import java.awt.*;

public class Main extends GeometryView3d {

    public static void main(String[] args) throws Exception {
        ViewerService viewerService = ViewerService.getInstance();
        var viewer = viewerService.getViewer().orElseThrow();
        viewer.open();

        viewer.addSphere(new Point3d(1, 1, 1), 3, Color.RED, "Test", false);
        viewer.addSphere(new Point3d(1, 1, 1), 4, Color.RED, "Test", true);
    }
}
