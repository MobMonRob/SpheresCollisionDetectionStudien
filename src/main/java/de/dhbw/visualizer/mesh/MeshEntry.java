package de.dhbw.visualizer.mesh;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Teapot;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;

public class MeshEntry extends Drawable implements Runnable {

    private static final int DIMENSIONS = 3;

    public static MeshEntry createDummyMesh() {
        var b = new float[]{
                1, 1, 1,
                0, 0, 0,
                0, 1, 0
        };
        return new MeshEntry();
    }

    public MeshEntry() {
        // super(points, pointDimensions);
        //setPosition(new Coord3d(0, 0, 0));
        setTransform(new Transform(new Rotate(90, new Coord3d())));
        //setColor(Color.RED);
    }

    private float angle = 0, shift = 0;

    @Override
    public void run() {
        var trans = new Transform(new Rotate(angle, new Coord3d(1, 1, 1)),
                new Scale(new Coord3d(angle, 1f, 1f))
              /*  new Translate(new Coord3d(1, shift, 1))*/);
        setTransform(trans);

        //applyGeometryTransform(trans);
        //  setColor(getColor() == Color.YELLOW ? Color.BLUE : Color.YELLOW);
        angle++;
        shift += 0.01f;
    }

    @Override
    public void draw(IPainter painter) {
        painter.glVertex3d(3f,3f,3f);
    }

    @Override
    public void applyGeometryTransform(Transform transform) {

    }

    @Override
    public void updateBounds() {

    }
}
