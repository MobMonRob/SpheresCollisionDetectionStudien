package de.dhbw.visualizer.mesh;

import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;

public class MeshEntry extends DrawableVBO2 {

    public MeshEntry(float[] points, int pointDimensions) {
        super(points, pointDimensions);
    }
}
