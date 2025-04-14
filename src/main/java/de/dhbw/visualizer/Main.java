package de.dhbw.visualizer;

import de.dhbw.visualizer.collision.SelfCollisionResult;
import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.collision.approximation.StlToSpheresGenerator;
import de.dhbw.visualizer.collision.distance.DefaultDistanceCalculator;
import de.dhbw.visualizer.collision.hierarchy.DivideAndConquerHierarchyTreeGenerator;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.TransformUtils;
import de.dhbw.visualizer.utils.LogUtils;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Urdf;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import org.jogamp.vecmath.Point3d;
import org.jzy3d.chart.factories.NewtChartFactory;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        LogUtils.setup();
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();
        var collisionDetection = SphereCollisionDetection.builder()
                .approximation(new StlToSpheresGenerator(0.05))
                .hierarchyTree(new DivideAndConquerHierarchyTreeGenerator())
                .distanceCalculator(new DefaultDistanceCalculator(0.0))
                .setRobotChain(chain)
                .build();

        collisionDetection.calculateStats();
        var selfCollision = collisionDetection.detectSelfCollision();
        LOGGER.info(() -> "Status: " + selfCollision.status() + " | Collision between " + selfCollision.collidedLinks().size() + " links");

        for (SelfCollisionResult.CollisionEntry collidedLink : selfCollision.collidedLinks()) {
            LOGGER.warning("Collision at " + collidedLink.link1() + " and " + collidedLink.link2());
        }

        launchVisualizer(collisionDetection);
    }

    private static void launchVisualizer(SphereCollisionDetection detection) throws Exception {
        Visualizer visualizer = new Visualizer();
        if (visualizer.getViewer() != null) {
            visualizer.getViewer().open();
            drawStl(visualizer.getViewer());

            for (List<Sphere> value : detection.getRawSpheres().values()) {
                drawSpheres(visualizer.getViewer(), value);
            }
        } else {
            LOGGER.warning("No Visualizer implementation found!");
        }
    }

    private static int counter = 0;

    private static Color getColor(int i) {
        counter++;
        return switch (i % 8) {
            case 0 -> Color.CYAN;
            case 1 -> Color.RED;
            case 2 -> Color.BLUE;
            case 3 -> Color.GREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.ORANGE;
            case 6 -> Color.MAGENTA;
            case 7 -> Color.PINK;
            default -> Color.BLACK;
        };
    }

    private static void drawStl(iEuclidViewer3D visualizer) {
        int i = 0;
        for (var entry : SphereCollisionDetection.VISUALIZATION.entrySet()) {
            var polygonPoints = entry.getKey().toPolygonPoints().stream()
                    .map(v -> new Point3d(v.x(), v.y(), v.z()))
                    .toList()
                    .toArray(value -> new Point3d[0]);
            var transformation = entry.getValue().getRPYXYZ();
            var solidColor = getColor(i);
            var color = new Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue(), 32);
            visualizer.addPolygone(new Point3d(polygonPoints[0]), polygonPoints, color, null, true, true, TransformUtils.transform(transformation));
            i++;
        }
    }

    private static void drawSpheres(iEuclidViewer3D visualizer, List<Sphere> spheres) {
        var color = getColor(0);
        for (Sphere sphere : spheres) {
            //  visualizer.addSphere(new Point3d(sphere.x(), sphere.y(), sphere.z()), sphere.radius(), color, "", true);
        }
    }
}
