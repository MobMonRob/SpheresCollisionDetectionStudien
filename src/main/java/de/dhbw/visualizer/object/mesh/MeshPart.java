package de.dhbw.visualizer.object.mesh;

import de.orat.math.xml.urdf.api.Chain;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transformer;
import org.jzy3d.plot3d.transform.Translate;

import java.util.List;

public class MeshPart extends Composite {

    public MeshPart(List<MeshVBO> initElements) {
        initElements.forEach(this::add);
    }

    public MeshPart translateAlongVector(float distance, Coord3d vector) {
        var translate = calculateTranslation(distance, vector);
        transform(translate);
        return this;
    }

    public MeshPart rotateAroundVector(float di, Coord3d vector) {
        var rotate = new Rotate(di, vector);
        transform(rotate);
        return this;
    }

    private Translate calculateTranslation(float distance, Coord3d vector) {
        var base = new Coord3d(vector.x, vector.y, vector.z)
                .normalizeTo(1.0f)
                .mul(distance);
        return new Translate(base);
    }

    private void transform(Transformer transformer) {
        var newObjects = this.getDrawables().stream()
                .map(d -> (MeshVBO) d)
                .map(vbo -> {
                    var translatedVbo = new MeshVBO(vbo.transform(transformer), 3);
                    translatedVbo.setColor(vbo.getColor());
                    return translatedVbo;
                })
                .toList();

        this.clear();
        newObjects.forEach(this::add);
    }
}
