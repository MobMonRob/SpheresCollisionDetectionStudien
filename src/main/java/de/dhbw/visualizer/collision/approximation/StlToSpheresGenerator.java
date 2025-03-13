package de.dhbw.visualizer.collision.approximation;

import de.dhbw.visualizer.Stl;
import de.dhbw.visualizer.collision.CollisionConstructionException;
import de.dhbw.visualizer.collision.SphereCollisionDetection;
import de.dhbw.visualizer.math.Sphere;
import de.dhbw.visualizer.math.TransformUtils;
import de.dhbw.visualizer.math.Triangle;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.visual.Mesh;
import de.orat.math.xml.urdf.visual.Shape;
import org.joml.Vector3d;
import org.jzy3d.maths.Coord3d;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StlToSpheresGenerator implements SphereGenerator {

    private static final Logger LOGGER = Logger.getLogger(StlToSpheresGenerator.class.getSimpleName());
    private final double radius;

    public StlToSpheresGenerator(double radius) {
        this.radius = radius;
    }

    @Override
    public List<Sphere> toSphere(Shape inputData, CollisionParameters link) {
        if (!(inputData instanceof Mesh mesh)) {
            throw new IllegalArgumentException("Invalid input type " + inputData.getClass().getName());
        }

        var result = new ArrayList<Sphere>();

        try {
            //TODO streamline file loading
            // LOGGER.info("Loading stl file from " + mesh.getPath());
            Stl stl = Stl.fromFile(new File(mesh.getPath()));
            SphereCollisionDetection.VISUALIZATION.put(stl, link);
            var transform = TransformUtils.transform(link.getRPYXYZ());
            for (Triangle triangle : stl.triangles()) {
                result.addAll(placeSpheres(triangle).stream()
                        .map(s -> {
                            var pos = transform.compute(new Coord3d(s.x, s.y, s.z));
                            return new Vector3d(pos.x, pos.y, pos.z);
                        })
                        .map(s -> new Sphere(s, radius))
                        .toList());
            }
        } catch (IOException ex) {
            throw new CollisionConstructionException(ex);
        }

        return result;
    }

    @Override
    public Shape.ShapeType getShapeType() {
        return Shape.ShapeType.mesh;
    }

    public List<Vector3d> placeSpheres(Triangle triangle) {
        var a = triangle.p1();
        var b = triangle.p2();
        var c = triangle.p3();

        List<Vector3d> spheres = new ArrayList<>();

        spheres.add(new Vector3d(a));
        spheres.add(new Vector3d(b));
        spheres.add(new Vector3d(c));

        spheres.add(midpoint(a, b));
        spheres.add(midpoint(b, c));
        spheres.add(midpoint(c, a));

        Vector3d centroid = new Vector3d(
                (a.x() + b.x() + c.x()) / 3,
                (a.y() + b.y() + c.y()) / 3,
                (a.z() + b.z() + c.z()) / 3
        );

        spheres.add(centroid);
        return spheres;
    }

    private Vector3d midpoint(Vector3d a, Vector3d b) {
        return new Vector3d((a.x + b.x) / 2, (a.y + b.y) / 2, (a.z + b.z) / 2);
    }
}
