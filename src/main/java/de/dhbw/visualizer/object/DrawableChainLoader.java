package de.dhbw.visualizer.object;

import de.dhbw.visualizer.object.mesh.DrawableChain;
import de.dhbw.visualizer.object.mesh.MeshPart;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.VisualParameters;
import de.orat.math.xml.urdf.visual.Mesh;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.RotationMatrix;
import org.jzy3d.plot3d.primitives.axis.Axis;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Translate;

import java.util.HashMap;
import java.util.Map;

public class DrawableChainLoader {

    public static void load(Chart chart, Chain chain) {
        //Map<String, MeshPart> meshes = new HashMap<>();
        double angle = 0;
        for (var value : chain.getLinks().values()) {
            for (VisualParameters visualParameter : value.getVisualParameters()) {
                if (visualParameter.getShape() instanceof Mesh mesh) {
                    var part = ObjectLoader.getCOLLADA(mesh.getPath());
                    angle += 100;
                    chart.add(part);

                    var matrix = value.getAbsTF();
                    var jomlMatrix = new Matrix4d(matrix.m00, matrix.m01, matrix.m02, matrix.m03,
                            matrix.m10, matrix.m11, matrix.m12, matrix.m13,
                            matrix.m20, matrix.m21, matrix.m22, matrix.m23,
                            matrix.m30, matrix.m31, matrix.m32, matrix.m33);
                    var translate = new Vector3d();
                    jomlMatrix.getTranslation(translate);

                    part.transform(new Transform(
                            new Scale(new Coord3d(1, 1, 1)),
                            new Translate(new Coord3d(translate.x(), translate.y(), translate.z()))
                            //   new Rotate(angle, new Coord3d(0, 1, 0))
                    ));
                }
            }
        }

        chart.getView().updateBounds();
        // var dh = chain.toDH();

        //return new DrawableChain(meshes, dh);
    }

    private DrawableChainLoader() {
    }
}
