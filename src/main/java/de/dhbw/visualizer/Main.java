package de.dhbw.visualizer;

import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.collision.approximation.StlToSpheresGenerator;
import de.dhbw.visualizer.collision.hierarchy.DivideAndConquerHierarchyTreeGenerator;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.Triangle;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.api.Urdf;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import org.jogamp.vecmath.Point3d;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();
        var collisionDetection = SphereCollisionDetection.builder()
                .approximation(new StlToSpheresGenerator(0.001, 5))
                .hierarchyTree(new DivideAndConquerHierarchyTreeGenerator())
                .setRobotChain(chain)
                .build();
        System.out.println(collisionDetection);

        launchVisualizer(collisionDetection);
    }

    private static void launchVisualizer(SphereCollisionDetection detection) throws Exception {
        Visualizer visualizer = new Visualizer();
        if (visualizer.getViewer() != null) {

            visualizer.getViewer().open();
            drawStl(visualizer.getViewer());
            for (Map.Entry<Link, List<Sphere>> entry : detection.getRawSpheres().entrySet()) {
                if (!entry.getKey().getName().equals("forearm_link"))
                    continue;
                var color = getColor();
                System.out.println(entry.getKey().getName());
                for (Sphere sphere : entry.getValue()) {
               //     visualizer.getViewer().addSphere(new Point3d(sphere.x(), sphere.y(), sphere.z()), sphere.radius(), color, entry.getKey().getName(), true);
                }
            }
        } else {
            System.out.println("No Visualizer implementation found!");
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

    private static void drawStl(iEuclidViewer3D visualizer) throws Exception {
        Stl stl = Stl.fromFile(new File("./meshes/ur5e/collision/forearm.stl"));
        var triangle = stl.triangles().get(0);

        drawTriangle(visualizer, triangle);

    }

    private static void drawTriangle(iEuclidViewer3D visualizer, Triangle triangle) {
        var color = getColor();
        var p1 = new Point3d(triangle.p1().x(), triangle.p1().y(), triangle.p1().z());
        var p2 = new Point3d(triangle.p2().x(), triangle.p2().y(), triangle.p2().z());
        var p3 = new Point3d(triangle.p3().x(), triangle.p3().y(), triangle.p3().z());
        visualizer.addPolygone(new Point3d(p1), new Point3d[]{p1, p2, p3}, color, "", false, false);

        var generator = new StlToSpheresGenerator(0.001, 5);
        double r = 0.001;
        var spheres = generator.placeSpheres(triangle, r, 100);
        drawSpheres(visualizer, spheres.stream().map(s -> new Sphere(s, r)).toList());
    }

    private static void drawSpheres(iEuclidViewer3D visualizer, List<Sphere> spheres) {
        var color = getColor();
        for (Sphere sphere : spheres) {
            visualizer.addSphere(new Point3d(sphere.x(), sphere.y(), sphere.z()), sphere.radius(), color, "", true);
        }
    }
}
