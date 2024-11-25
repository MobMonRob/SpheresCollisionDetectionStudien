package de.dhbw.visualizer.mesh;

import de.dhbw.visualizer.object.ObjectLoader;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.api.VisualParameters;
import de.orat.math.xml.urdf.visual.Mesh;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;

import java.util.Map;
import java.util.logging.Logger;

public class ChainMesh extends Composite {

    private static final Logger LOGGER = Logger.getLogger(ChainMesh.class.getName());
   // private static final ObjectLoader OBJECT_LOADER = ObjectLoader.getLoader();

    private final Chain chain;
    private final Map<String, Chain.DH> dhParameters;

    public ChainMesh(Chain chain) {
        this.chain = chain;
        this.dhParameters = chain.toDH();
        for (Map.Entry<String, Link> entry : this.chain.getLinks().entrySet()) {
            initLink(entry.getValue());
        }
    }

    public void initLink(Link link) {
        for (VisualParameters parameter : link.getVisualParameters()) {
            this.initVisualParameters(link, parameter);
        }
    }

    private void initVisualParameters(Link link, VisualParameters parameters) {
        var shape = parameters.getShape();
        var type = shape.getShapeType();

        if (shape instanceof Mesh mesh) {
            LOGGER.info(mesh.getPath());
            var part = ObjectLoader.getCOLLADA(mesh.getPath());
          //  part.rotateAroundVector(1, new Coord3d(1, 1, 1));
           // add(part.getParts());
        } else {
            throw new IllegalArgumentException(String.format("Unknown shape type: %s", shape));
        }
    }
}
