package de.dhbw.visualizer.object.mesh;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.transform.Transformer;

import java.util.Arrays;

public class MeshVBO extends DrawableVBO2 {

    private final float[] copyOfVertices;

    public MeshVBO(float[] points, int pointDimensions) {
        super(points, pointDimensions);

        this.copyOfVertices = Arrays.copyOf(points, points.length);
        this.setWireframeDisplayed(false);
    }

    public float[] transform(Transformer translate) {
        float[] newVertices = new float[this.copyOfVertices.length];

        for (int i = 0; i < newVertices.length - 2; i += 3) {
            Coord3d newCoord = new Coord3d(this.copyOfVertices[i], this.copyOfVertices[i + 1], this.copyOfVertices[i + 2]);
            newCoord = translate.compute(newCoord);
            newVertices[i] = newCoord.x;
            newVertices[i + 1] = newCoord.y;
            newVertices[i + 2] = newCoord.z;
        }

        return newVertices;
    }
}
