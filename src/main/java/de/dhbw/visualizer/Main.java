package de.dhbw.visualizer;

import de.dhbw.visualizer.collision.SelfCollisionResult;
import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.collision.approximation.StlToSpheresGenerator;
import de.dhbw.visualizer.collision.distance.DefaultDistanceCalculator;
import de.dhbw.visualizer.collision.hierarchy.DivideAndConquerHierarchyTreeGenerator;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.Triangle;
import de.dhbw.visualizer.utils.LogUtils;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Urdf;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import org.jogamp.vecmath.Point3d;

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
                .approximation(new StlToSpheresGenerator(0.01, 5))
                .hierarchyTree(new DivideAndConquerHierarchyTreeGenerator())
                .distanceCalculator(new DefaultDistanceCalculator(0.0))
                .setRobotChain(chain)
                .build();

        collisionDetection.calculateStats();
        var selfCollision = collisionDetection.detectSelfCollision();
        LOGGER.info("Status: " + selfCollision.status() + " | Collision between " + selfCollision.collidedLinks().size() + " links");

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

    private static Color getColor() {
        counter++;
        return switch (counter % 8) {
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
        for (Stl stl : Stl.LOADED_STL) {
            var point3ds = stl.toPolygonPoints().stream()
                    .map(v -> new Point3d(v.x(), v.y(), v.z()))
                    .toList()
                    .toArray(value -> new Point3d[0]);

            var l = visualizer.addPolygone(new Point3d(point3ds[0]), point3ds, getColor(), null, true, true);
        }
    }

    private static void drawTriangle(iEuclidViewer3D visualizer, Triangle triangle) {
        var color = getColor();
        var p1 = new Point3d(triangle.p1().x(), triangle.p1().y(), triangle.p1().z());
        var p2 = new Point3d(triangle.p2().x(), triangle.p2().y(), triangle.p2().z());
        var p3 = new Point3d(triangle.p3().x(), triangle.p3().y(), triangle.p3().z());
        visualizer.addPolygone(new Point3d(p1), new Point3d[]{p1, p2, p3}, color, null, false, false);

        double r = 0.01;

        var generator = new StlToSpheresGenerator(r, 5);
        var spheres = generator.placeSpheres(triangle, r, 100);

        //  drawSpheres(visualizer, spheres.stream().map(s -> new Sphere(s, r)).toList());
    }

    private static void drawSpheres(iEuclidViewer3D visualizer, List<Sphere> spheres) {
        var color = getColor();
        for (Sphere sphere : spheres) {
            visualizer.addSphere(new Point3d(sphere.x(), sphere.y(), sphere.z()), sphere.radius(), color, "", true);
        }
    }
}
