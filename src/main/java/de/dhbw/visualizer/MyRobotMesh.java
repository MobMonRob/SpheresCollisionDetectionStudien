package de.dhbw.visualizer;

import de.orat.math.view.euclidview3d.impl.EuclidViewer3D;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.api.VisualParameters;
import de.orat.math.xml.urdf.visual.Mesh;
import de.orat.math.xml.urdf.visual.Shape;
import org.jogamp.vecmath.Matrix4d;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Pair;
import org.jzy3d.plot3d.primitives.EuclidPart;
import org.jzy3d.plot3d.primitives.EuclidRobot;
import org.jzy3d.plot3d.primitives.RobotType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class MyRobotMesh {

    public void add(EuclidViewer3D viewer3D, List<Pair<Link, Chain.DH>> data) {
        List<String> components = new ArrayList<>();
        List<Chain.DH> dhParameter = new ArrayList<>();

        for (Pair<Link, Chain.DH> entry : data) {
            var link = entry.a;
            var dh = entry.b;

            for (VisualParameters visualParameter : link.getVisualParameters()) {
                if (visualParameter.getShape() instanceof Mesh mesh) {
                    components.add(mesh.getPath());
                    dhParameter.add(dh);
                }
            }
        }

        Double[] theta = dhParameter.stream().map(Chain.DH::theta).toArray(Double[]::new);
        Double[] alpha = dhParameter.stream().map(Chain.DH::alpha).toArray(Double[]::new);
        Double[] d = dhParameter.stream().map(Chain.DH::d).toArray(Double[]::new);
        Double[] r = dhParameter.stream().map(Chain.DH::r).toArray(Double[]::new);

        for (String component : components) {
            viewer3D.addMesh(component,new Matrix4d());
        }
        //this.getChart().add(new EuclidPart())
     /*   setDataWithUR5eDHDeltas(components,
                toPrimitiveArray(theta),
                toPrimitiveArray(alpha),
                toPrimitiveArray(d),
                toPrimitiveArray(r)
        );
        chart.addM*/
    }

    private double[] toPrimitiveArray(Double[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}
