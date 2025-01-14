package de.dhbw.visualizer.object.mesh;

import org.joml.Vector3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshVBO extends DrawableVBO2 {

    private final float[] copyOfVertices;

    public MeshVBO(float[] points, int pointDimensions) {
        super(points, pointDimensions);

        this.copyOfVertices = Arrays.copyOf(points, points.length);

        this.setWireframeDisplayed(false);
    }

    @Override
    public void setTransform(Transform transform) {
        super.setTransform(transform);
    }

    public List<Vector3d> getVerticesAsVector() {
        List<Vector3d> vertices = new ArrayList<>();
        for (int i = 0; i < this.copyOfVertices.length - 3; i += 3) {
            vertices.add(new Vector3d(copyOfVertices[i], copyOfVertices[i + 1], copyOfVertices[i + 3]));
        }

        return vertices;
    }
}
