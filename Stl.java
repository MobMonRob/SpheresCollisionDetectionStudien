package de.dhbw.visualizer.stl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

// Simple stl parser. 
// TODOs: 
//  - Refactoring
//  - Header validation
//  - ASCII Support
public class Stl {

    public static void main(String[] args) {
        try (RandomAccessFile aFile = new RandomAccessFile("arm_flanche_angle.stl", "r");
             FileChannel inChannel = aFile.getChannel();) {

            long fileSize = inChannel.size();

            //Create buffer of the file size
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            inChannel.read(buffer);
            buffer.flip();

            // Verify the file content
            var read = new Stl().read(buffer);
            for (var triangle : read) {
                System.out.println(triangle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Triangle> read(ByteBuffer byteBuffer) {
        List<Triangle> trianglesRes = new ArrayList<>();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 80; i++) {
            byteBuffer.get();
        }

        int triangles = byteBuffer.getInt();

        for (int i = 0; i < triangles; i++) {
            float nX = byteBuffer.getFloat();
            float nY = byteBuffer.getFloat();
            float nZ = byteBuffer.getFloat();

            var p1 = new Vec3f(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            var p2 = new Vec3f(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            var p3 = new Vec3f(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());

            short abr = byteBuffer.getShort();
            trianglesRes.add(new Triangle(p1, p2, p3));
        }

        if (byteBuffer.hasRemaining()) {
            throw new IllegalStateException(String.format("stl buffer has remaining elements. (pos %d remaining %d)",
                    byteBuffer.position(), byteBuffer.remaining()));
        }
        return trianglesRes;
    }

    public record Vec3f(float x, float y, float z) {
    }

    public record Triangle(Vec3f p1, Vec3f p2, Vec3f p3) {

    }
}
