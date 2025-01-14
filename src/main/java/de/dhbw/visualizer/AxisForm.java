package de.dhbw.visualizer;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.transform.Rotate;

public class AxisForm {

    public static void main(String[] args) {
        // Beispiel: RPY-Winkel (Roll=0, Pitch=0, Yaw=3.141592653589793)
        double roll = 0;
        double pitch = 0;
        double yaw = Math.PI;

        var axisAngle = rpyToAxisAngle(roll, pitch, yaw);

    //    System.out.println("Rotationsachse: (" + axisAngle[0] + ", " + axisAngle[1] + ", " + axisAngle[2] + ")");
     //   System.out.println("Rotationswinkel: " + axisAngle[3] + " rad");
    }

    public static Rotate rpyToAxisAngle(double roll, double pitch, double yaw) {
        // 1. Zuerst die RPY-Winkel in eine Rotationsmatrix umwandeln
        double[][] rotationMatrix = new double[3][3];

        // Rotation um die X-Achse (Roll)
        double[][] rotX = {
                {1, 0, 0},
                {0, Math.cos(roll), -Math.sin(roll)},
                {0, Math.sin(roll), Math.cos(roll)}
        };

        // Rotation um die Y-Achse (Pitch)
        double[][] rotY = {
                {Math.cos(pitch), 0, Math.sin(pitch)},
                {0, 1, 0},
                {-Math.sin(pitch), 0, Math.cos(pitch)}
        };

        // Rotation um die Z-Achse (Yaw)
        double[][] rotZ = {
                {Math.cos(yaw), -Math.sin(yaw), 0},
                {Math.sin(yaw), Math.cos(yaw), 0},
                {0, 0, 1}
        };

        // Kombinieren der Rotationsmatrizen: R = rotZ * rotY * rotX
        rotationMatrix = multiplyMatrices(rotZ, multiplyMatrices(rotY, rotX));

        // 2. Aus der Rotationsmatrix die Axis-Angle-Darstellung berechnen
        double angle = Math.acos((rotationMatrix[0][0] + rotationMatrix[1][1] + rotationMatrix[2][2] - 1) / 2);

        double x = rotationMatrix[2][1] - rotationMatrix[1][2];
        double y = rotationMatrix[0][2] - rotationMatrix[2][0];
        double z = rotationMatrix[1][0] - rotationMatrix[0][1];

        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }

        return new Rotate(Math.toDegrees(angle), new Coord3d(x, y, z));
    }

    /**
     * Multipliziert zwei Matrizen.
     *
     * @param a Erste Matrix
     * @param b Zweite Matrix
     * @return Das Produkt der beiden Matrizen
     */
    private static double[][] multiplyMatrices(double[][] a, double[][] b) {
        int rows = a.length;
        int cols = b[0].length;
        int commonDim = b.length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < commonDim; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }
}
