package de.dhbw.visualizer.jurdf;

import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.CollisionParameters;
import de.orat.math.xml.urdf.api.Joint;
import de.orat.math.xml.urdf.api.Link;
import lombok.Getter;
import org.jogamp.vecmath.Vector3d;
import org.joml.Matrix4d;

import java.util.ArrayList;
import java.util.List;

/**
 * A ProcessChain is a list of pre-calculated links in a {@link Chain}.
 * The {@link de.orat.math.xml.urdf.api.Chain.RPYXYZ} attributes are modified so that they match their absolute values.
 *
 * @author lhert
 */
@Getter
public class ProcessedChain {

    public static ProcessedChain chain(Chain chain) {
        return new ProcessedChain(chain);
    }

    private final List<Link> links = new ArrayList<>();

    private ProcessedChain(Chain chain) {
        recursive(chain.getRootLink());
    }

    private void recursive(Link link) {
        recursive(null, link);
    }

    private void recursive(Chain.RPYXYZ parent, Link link) {
        var newLink = new Link(link.getName());

        for (CollisionParameters collisionParameter : link.getCollisionParameters()) {
            var transform = applyTransformation(parent, collisionParameter.getRPYXYZ());
            var newParameter = new CollisionParameters(transform, collisionParameter.getShape());
            newLink.getCollisionParameters().add(newParameter);
        }

        this.links.add(newLink);

        for (Joint joint : link.getChildren()) {
            recursive(applyTransformation(parent, joint.getRPYXYZ()), joint.getChild());
        }
    }

    private static Chain.RPYXYZ applyTransformation(Chain.RPYXYZ parent, Chain.RPYXYZ current) {
        if (parent == null) {
            return current;
        }

        // Get rotation/translation from joml matrix.
        Matrix4d global = homogeneousTransform(parent)
                .mul(homogeneousTransform(current), new Matrix4d());

        var translation = global.getTranslation(new org.joml.Vector3d());
        var rpyFromMatrix = global.getEulerAnglesZYX(new org.joml.Vector3d());

        // Transform to jogamp data types.
        var newPosition = new Vector3d(translation.x(), translation.y(), translation.z());
        var newRPY = new Chain.RPY(rpyFromMatrix.x(), rpyFromMatrix.y(), rpyFromMatrix.z());
        return new Chain.RPYXYZ(newRPY, newPosition);
    }

    private static Matrix4d rotationMatrix(Chain.RPY rpy) {
        return new Matrix4d()
                .rotateZ(rpy.yaw())
                .rotateY(rpy.pitch())
                .rotateX(rpy.roll());
    }

    private static Matrix4d homogeneousTransform(Chain.RPYXYZ rpyxyz) {
        return new Matrix4d()
                .translate(new org.joml.Vector3d(rpyxyz.xyz().x, rpyxyz.xyz().y, rpyxyz.xyz().z))
                .mul(rotationMatrix(rpyxyz.rpy()));
    }
}
