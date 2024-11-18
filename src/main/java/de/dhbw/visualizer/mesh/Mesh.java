package de.dhbw.visualizer.mesh;

import org.apache.commons.lang3.NotImplementedException;
import org.jogamp.vecmath.Matrix4d;
import org.jzy3d.plot3d.primitives.Composite;

import java.util.List;

public class Mesh extends Composite {


    private Mesh(final List<MeshEntry> entries) {
        entries.forEach(this::add);
    }

    public void transform(Matrix4d transformation) {
        throw new NotImplementedException();
    }

}
