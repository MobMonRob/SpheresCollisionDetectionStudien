package de.dhbw.visualizer.object.mesh;

import lombok.Getter;
import lombok.Setter;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Transformer;
import org.jzy3d.plot3d.transform.Translate;

import java.util.List;

public class MeshPart extends Composite {

    @Getter
    @Setter
    private Coord3d localVectorSystemX;
    @Getter
    @Setter
    private Coord3d localVectorSystemY;
    @Getter
    @Setter
    private Coord3d localVectorSystemZ;
    @Getter
    @Setter
    private Coord3d center;

    public MeshPart(List<MeshVBO> initElements) {
        initElements.forEach(this::add);

        localVectorSystemX = new Coord3d(1, 0, 0);
        localVectorSystemY = new Coord3d(0, 1, 0);
        localVectorSystemZ = new Coord3d(0, 1, 0);
        center = new Coord3d(0.0F, 0.0F, 0.0F);
    }

    public MeshPart translateAlongVector(float distance, Coord3d vector) {
        var translate = calculateTranslation(distance, vector);
        // transform(translate);

        return this;
    }

    public MeshPart rotateAroundVector(float di, Coord3d vector) {
        var rotate = new Rotate(di, vector);
        // transform(rotate);
        return this;
    }

    private Translate calculateTranslation(float distance, Coord3d vector) {
        var base = new Coord3d(vector.x, vector.y, vector.z)
                .normalizeTo(1.0f)
                .mul(distance);
        return new Translate(base);
    }

    public void transform(Transform transformer) {
       /* var newObjects = this.getDrawables().stream()
                .map(d -> (MeshVBO) d)
                .map(vbo -> {
                  //  var translatedVbo = new MeshVBO(vbo.transform(transformer), 3);
                 //   translatedVbo.setColor(vbo.getColor());
                 //   return translatedVbo;
                })
                .toList();

        this.clear();
        newObjects.forEach(this::add);*/

        setTransform(transformer);
        //setTransformBefore(transformer);
    }

    @Override
    public void setTransform(Transform transform) {
        if (!transform.toString().contains("Translate")) {
            return;
        }
        super.setTransform(transform);
    }
}
