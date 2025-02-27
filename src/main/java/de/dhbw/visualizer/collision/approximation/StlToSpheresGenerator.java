package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.Stl;
import de.dhbw.visualizer.collision.CollisionConstructionException;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.Triangle;
import de.orat.math.xml.urdf.visual.Mesh;
import de.orat.math.xml.urdf.visual.Shape;
import org.joml.Vector3d;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StlToSpheresGenerator implements SphereGenerator {

    private static final Logger LOGGER = Logger.getLogger(StlToSpheresGenerator.class.getSimpleName());
    private final double radius;
    private final int layers;

    public StlToSpheresGenerator(double radius, int layers) {
        this.radius = radius;
        this.layers = layers;
    }

    @Override
    public List<Sphere> toSphere(Shape inputData) {
        if (!(inputData instanceof Mesh mesh)) {
            throw new IllegalArgumentException("Invalid input type " + inputData.getClass().getName());
        }

        var result = new ArrayList<Sphere>();

        try {
            //TODO streamline file loading
            // LOGGER.info("Loading stl file from " + mesh.getPath());
            Stl stl = Stl.fromFile(new File(mesh.getPath()));

            for (Triangle triangle : stl.triangles()) {
                result.addAll(placeSpheres(triangle, this.radius, this.layers).stream()
                        .map(s -> new Sphere(s, radius))
                        .toList());
            }
        } catch (IOException ex) {
            throw new CollisionConstructionException(ex);
        }

        //result.add(new Sphere(new Vector3d(0, 0, 0), 2));
        return result;
    }

    @Override
    public Shape.ShapeType getShapeType() {
        return Shape.ShapeType.mesh;
    }

    public List<Vector3d> placeSpheres(Triangle triangle, double r, int layers) {
        var a = triangle.p1();
        var b = triangle.p2();
        var c = triangle.p3();

        List<Vector3d> spheres = new ArrayList<>();
        spheres.add(new Vector3d(a));
        spheres.add(new Vector3d(b));
        spheres.add(new Vector3d(c));

        //TODO scan conversation
        /*double heightStep = 2 * r * Math.sqrt(2.0 / 3.0); // Abstand der Schichten (HCP)

        // Normale zum Dreieck berechnen
        var ab = new Vector3d(b).sub(a);
        var ac = new Vector3d(c).sub(a);
        var normal = ab.cross(ac).normalize();

        double minX = Math.min(a.x, Math.min(b.x, c.x));
        double maxX = Math.max(a.x, Math.max(b.x, c.x));
        double minY = Math.min(a.y, Math.min(b.y, c.y));
        double maxY = Math.max(a.y, Math.max(b.y, c.y));

        // Hexagonal shift
        boolean shiftRow = false;

        for (int layer = 0; layer < layers; layer++) {
            double zOffset = layer * heightStep;
            var layerOffset = new Vector3d(normal).mul(zOffset);

            for (double y = minY; y <= maxY; y += heightStep) {
                for (double x = minX + (shiftRow ? r : 0); x <= maxX; x += 2 * r) {
                    var candidate = new Vector3d(x, y, 0).add(layerOffset);
                    if (isPointInTriangle(candidate, a, b, c)) {
                        spheres.add(candidate);
                    }
                }
                shiftRow = !shiftRow;
            }
        }*/

        return spheres;
    }

    private static boolean isPointInTriangle(Vector3d p, Vector3d a, Vector3d b, Vector3d c) {
        Vector3d v0 = new Vector3d(c).sub(a);
        Vector3d v1 = new Vector3d(b).sub(a);
        Vector3d v2 = new Vector3d(p).sub(a);

        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }
}
