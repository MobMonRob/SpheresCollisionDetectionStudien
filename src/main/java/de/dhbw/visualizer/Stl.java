package de.dhbw.visualizer;

import de.dhbw.visualizer.math.Triangle;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3d;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @see <a href="https://en.wikipedia.org/wiki/STL_(file_format)">STL (file format)</a>
 */
public record Stl(
        String name,
        List<Triangle> triangles
) {

    public List<Vector3d> toPolygonPoints() {
        List<Vector3d> points = new ArrayList<>();

        for (Triangle triangle : triangles) {
            points.add(triangle.p1());
            points.add(triangle.p2());
            points.add(triangle.p3());
        }

        return points;
    }

    public static Stl fromFile(File file) throws IOException {
        try (var accessFile = new RandomAccessFile(file, "r");
             var channel = accessFile.getChannel()) {
            long size = channel.size();
            var buffer = ByteBuffer.allocate((int) size);
            channel.read(buffer);
            buffer.flip();
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            return fromBuffer(file.getName(), buffer);
        }
    }

    public static Stl fromStream(String name, InputStream inputStream) throws IOException {
        var bytes = IOUtils.toByteArray(inputStream);
        return fromBuffer(name, ByteBuffer.wrap(bytes));
    }

    private static Stl fromBuffer(String name, ByteBuffer byteBuffer) throws IOException {
        // Read stl header
        var header = new byte[80];
        byteBuffer.get(header);
        Stl stl;
        if (isAscii(header)) {
            byteBuffer.position(0);
            stl = fromAscii(name, byteBuffer);
        } else {
            stl = fromBinary(name, byteBuffer);
        }

        return stl;
    }

    private static Stl fromBinary(String name, ByteBuffer byteBuffer) throws IOException {
        var triangles = new ArrayList<Triangle>();

        // Read elements
        int elements = byteBuffer.getInt();

        for (int i = 0; i < elements; i++) {
            var normal = new Vector3d(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            var p1 = new Vector3d(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            var p2 = new Vector3d(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            var p3 = new Vector3d(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());

            // We don't use any attributes, so we just skip the attribute bytes.
            short attributes = byteBuffer.getShort();
            byte[] attributeData = new byte[attributes];
            byteBuffer.get(attributeData);

            triangles.add(new Triangle(normal, p1, p2, p3));
        }

        // Check if the buffer is empty
        if (byteBuffer.hasRemaining()) {
            throw new IOException(String.format("stl buffer has remaining elements. (pos %d remaining %d)",
                    byteBuffer.position(), byteBuffer.remaining()));
        }

        return new Stl(name, triangles);
    }

    private static Stl fromAscii(String name, ByteBuffer byteBuffer) throws IOException {
        var triangles = new ArrayList<Triangle>();

        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        var string = new String(data, StandardCharsets.UTF_8);
        var stringTokenizer = new StringTokenizer(string);

        // Skip header tokens
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();

        while (stringTokenizer.hasMoreTokens()) {
            var token = stringTokenizer.nextToken();

            // EOF
            if ("endsolid".equals(token)) {
                continue;
            }

            // Read block
            var triangle = parseAsciiBlock(stringTokenizer);
            triangles.add(triangle);
        }

        if (stringTokenizer.hasMoreTokens()) {
            throw new IOException("Tokenizer must be empty at this point.");
        }

        return new Stl(name, triangles);
    }

    private static Triangle parseAsciiBlock(StringTokenizer tokenizer) throws IOException {
        requireToken(tokenizer, "normal");

        var normal = parseVector(tokenizer);

        requireToken(tokenizer, "outer");
        requireToken(tokenizer, "loop");

        var p1 = parseVertex(tokenizer);
        var p2 = parseVertex(tokenizer);
        var p3 = parseVertex(tokenizer);

        requireToken(tokenizer, "endloop");
        requireToken(tokenizer, "endfacet");

        return new Triangle(normal, p1, p2, p3);
    }

    private static Vector3d parseVertex(StringTokenizer tokenizer) throws IOException {
        if (!"vertex".equals(tokenizer.nextToken())) {
            throw new IOException("STL ASCII parse error. Expected token: vertex");
        }
        return new Vector3d(Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()));
    }

    private static void requireToken(StringTokenizer tokenizer, String expected) throws IOException {
        var token = tokenizer.nextToken();

        if (!expected.equals(token)) {
            throw new IOException("STL ASCII parse error. Expected token: " + expected + " got " + token);
        }
    }

    private static Vector3d parseVector(StringTokenizer tokenizer) {
        return new Vector3d(Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()));
    }

    private static boolean isAscii(byte[] header) {
        return header[0] == 's' && header[1] == 'o' && header[2] == 'l' && header[3] == 'i' && header[4] == 'd';
    }
}