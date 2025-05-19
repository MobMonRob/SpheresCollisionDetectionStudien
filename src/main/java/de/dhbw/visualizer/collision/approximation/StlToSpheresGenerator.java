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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StlToSpheresGenerator implements SphereGenerator {

    private final double radius;

    public StlToSpheresGenerator(double radius) {
        this.radius = radius;
    }

    @Override
    public List<Sphere> toSphere(String name,Shape inputData, CollisionParameters link) {
        if (!(inputData instanceof Mesh mesh)) {
            throw new IllegalArgumentException("Invalid input type " + inputData.getClass().getName());
        }

        var result = new ArrayList<Sphere>();

        try {
            Stl stl = Stl.fromFile(new File(mesh.getPath()));
            link.setName(name);
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

        Set<Sphere> spheres = new HashSet<>();
        spheres.addAll(this.placeSpheresOnLine(new Vector3d(a), new Vector3d(b)));
        spheres.addAll(this.placeSpheresOnLine(new Vector3d(a), new Vector3d(c)));
        spheres.addAll(this.placeSpheresOnLine(new Vector3d(b), new Vector3d(c)));

        return new ArrayList<>(spheres);
    }

    private Set<Sphere> placeSpheresOnLine(Vector3d start, Vector3d end) {
        Set<Sphere> centers = new HashSet<>();

        Vector3d direction = new Vector3d(end).sub(start);
        double distance = direction.length();
        Vector3d unitDirection = direction.normalize();

        double spacing = 2 * this.radius;
        int numSpheres = (int) (distance / spacing) + 1;

        for (int i = 0; i < numSpheres; i++) {
            Vector3d point = new Vector3d(start).add(new Vector3d(unitDirection).mul(i * spacing));
            centers.add(new Sphere(point, this.radius));
        }

        return centers;
    }

}
