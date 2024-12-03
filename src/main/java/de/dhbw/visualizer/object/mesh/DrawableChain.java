package de.dhbw.visualizer.object.mesh;

import com.google.common.base.Preconditions;
import de.orat.math.xml.urdf.api.Chain;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;

import java.util.Map;

public class DrawableChain extends Composite {

    private final Map<String, MeshPart> parts;
    private final Map<String, Chain.DH> dhMap;

    public DrawableChain(Map<String, MeshPart> parts, Map<String, Chain.DH> dh) {
        this.parts = parts;
        this.dhMap = dh;

        for (MeshPart value : parts.values()) {
            add(value);
        }

        moveDH();
    }

    public void moveDH() {
        MeshPart previous = null;
        for (String key : parts.keySet()) {
            moveDH(key, previous);
            previous = parts.get(key);
        }
    }

    private void moveDH(String key, MeshPart previous) {
        var part = parts.get(key);
        var dh = dhMap.get(key);

        Preconditions.checkArgument(part != null && dh != null);

        if (previous != null) {
            translateD(part, previous, dh);
        }

        rotateAlpha(part, dh);
        translateR(part, dh);
    }

  /*  private void rotateTheta(MeshPart part, MeshPart previous, MeshPart center, Chain.DH dh) {
        if (dh.theta() == 0) {
            return;
        }

        Coord3d z = previous.getLocalVectorSystemZ();
        Coord3d around = z;
        //Rotate the Parts
        //TODO may be replaced with rotateAroundVector2?
        part.rotateAroundVector((float) dh.theta(), around, center.getCenter());
        //Rotate Coordinate Systems
        Coord3d x = part.getLocalVectorSystemX();
        Coord3d newX = x.rotate((float) dh.theta(), around);
        part.setLocalVectorSystemX(newX);
        Coord3d newZ = part.getLocalVectorSystemZ();
        newZ = newZ.rotate((float) dh.theta(), around);
        part.setLocalVectorSystemZ(newZ);
        //Rotate the center
        if (j != i) {
            part.rotateCenter((float) dh.theta(), around, center.getCenter());
        }
    }*/

    private void translateD(MeshPart part, MeshPart previous, Chain.DH dh) {
        if (dh.d() == 0) {
            return;
        }

        Coord3d c = part.getCenter();
        Coord3d z = previous.getLocalVectorSystemZ();

        float d = (float) dh.d();
        part.translateAlongVector(d, z);
        part.setCenter(new Coord3d(c.x + d * z.x, c.y + d * z.y, c.z + d * z.z));
    }

    private void translateR(MeshPart part, Chain.DH dh) {
        if (dh.r() == 0) {
            return;
        }

        var center = part.getCenter();
        var x = part.getLocalVectorSystemX();
        x = new Coord3d(Math.round(x.x), Math.round(x.y), Math.round(x.z));

        var r = dh.r();

        part.translateAlongVector((float) r, x);
        part.setCenter(new Coord3d(center.x + r * x.x, center.y + r * x.y, center.z + r * x.z));
    }

    private void rotateAlpha(MeshPart part, Chain.DH dh) {
        if (dh.alpha() == 0) {
            return;
        }

        float alpha = (float) dh.alpha();
        if (alpha < 0) {
            alpha = 360 + alpha;
        }

        var newZ = part.getLocalVectorSystemZ();
        var x = part.getLocalVectorSystemX();
        x = new Coord3d(Math.round(x.x), Math.round(x.y), Math.round(x.z));

        part.setLocalVectorSystemZ(newZ.rotate(alpha, x));
    }
}
