package de.dhbw.visualizer.math;

import de.orat.math.xml.urdf.api.Chain;
import org.jogamp.vecmath.Quat4d;
import org.joml.AxisAngle4d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.transform.Rotate;

public class TransformUtils {

    public static Rotate rotationFromEulerAngles(Chain.RPYXYZ rpyxyz) {
        return rotationFromEulerAngles(rpyxyz.rpy().yaw(), rpyxyz.rpy().pitch(), rpyxyz.rpy().roll());
    }

    public static Rotate rotationFromEulerAngles(double yaw, double pitch, double roll) {
        Quat4d quaternion = eulerToQuaternion(roll, pitch, yaw);
        AxisAngle4d axisAngle = quaternionToAxisAngle(quaternion);

        return new Rotate(Math.toDegrees(axisAngle.angle), new Coord3d(axisAngle.x, axisAngle.y, axisAngle.z));
    }

    private static Quat4d eulerToQuaternion(double roll, double pitch, double yaw) {
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);

        Quat4d q = new Quat4d();
        q.w = cr * cp * cy + sr * sp * sy;
        q.x = sr * cp * cy - cr * sp * sy;
        q.y = cr * sp * cy + sr * cp * sy;
        q.z = cr * cp * sy - sr * sp * cy;
        return q;
    }

    private static AxisAngle4d quaternionToAxisAngle(Quat4d q) {
        q.normalize();

        AxisAngle4d axisAngle = new AxisAngle4d();

        axisAngle.angle = 2.0d * Math.acos(q.w);

        double sinHalfAngle = Math.sqrt(1.0f - q.w * q.w);

        if (sinHalfAngle > 0.0001f) {
            axisAngle.x = q.x / sinHalfAngle;
            axisAngle.y = q.y / sinHalfAngle;
            axisAngle.z = q.z / sinHalfAngle;
        } else {
            axisAngle.x = 1.0f;
            axisAngle.y = 0.0f;
            axisAngle.z = 0.0f;
        }

        return axisAngle;
    }

    private TransformUtils() {
    }

}
