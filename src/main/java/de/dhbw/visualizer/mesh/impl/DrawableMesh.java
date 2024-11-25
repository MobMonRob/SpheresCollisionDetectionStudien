package de.dhbw.visualizer.mesh.impl;

import org.jzy3d.plot3d.primitives.EuclidPart;
import org.jzy3d.plot3d.primitives.EuclidVBO2;

import java.util.List;

public class DrawableMesh extends EuclidPart {

    public DrawableMesh(List<EuclidVBO2> parts) {
        super(parts);
    }
}
