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
            Stl stl = Stl.fromFile(new File(mesh.getPath()));
            SphereCollisionDetection.VISUALIZATION.put(stl, link);
            var transform = TransformUtils.transform(link.getRPYXYZ());
            for (Triangle triangle : stl.triangles()) {
                result.addAll(placeSpheres(triangle).stream()
                        .map(sphere -> {
                            var center = sphere.center();
                            var pos = transform.compute(new Coord3d(center.x, center.y, center.z));
                            return new Sphere(new Vector3d(pos.x, pos.y, pos.z), sphere.radius());
                        })
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

    public List<Sphere> placeSpheres(Triangle triangle) {
        var a = triangle.p1();
        var b = triangle.p2();
        var c = triangle.p3();

        List<Sphere> spheres = new ArrayList<>();
        spheres.addAll(this.placeSpheresOnLine(a, b));
        spheres.addAll(this.placeSpheresOnLine(a, c));
        spheres.addAll(this.placeSpheresOnLine(b, c));

        return spheres;
    }

    private List<Sphere> placeSpheresOnLine(Vector3d p1, Vector3d p2) {
        var minX = Math.min(p1.x(), p2.x());
        var minY = Math.min(p1.y(), p2.y());
        var minZ = Math.min(p1.z(), p2.z());

        var maxX = Math.max(p1.x(), p2.x());
        var maxY = Math.max(p1.y(), p2.y());
        var maxZ = Math.max(p1.z(), p2.z());

        List<Sphere> spheres = new ArrayList<>();

        for (double x = minX; x < maxX; x += this.radius) {
            for (double y = minY; y < maxY; y += this.radius) {
                for (double z = minZ; z < maxZ; z += this.radius) {
                    spheres.add(new Sphere(new Vector3d(x, y, z), this.radius));
                }
            }
        }

        return spheres;
    }

    private Vector3d midpoint(Vector3d a, Vector3d b) {
        return new Vector3d((a.x + b.x) / 2, (a.y + b.y) / 2, (a.z + b.z) / 2);
    }
}
