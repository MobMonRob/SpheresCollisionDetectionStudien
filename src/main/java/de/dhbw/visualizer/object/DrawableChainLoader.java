package de.dhbw.visualizer.object;

import de.dhbw.visualizer.object.mesh.DrawableChain;
import de.dhbw.visualizer.object.mesh.MeshPart;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.VisualParameters;
import de.orat.math.xml.urdf.visual.Mesh;

import java.util.HashMap;
import java.util.Map;

public class DrawableChainLoader {

    public static DrawableChain load(Chain chain) {
        Map<String, MeshPart> meshes = new HashMap<>();

        for (var value : chain.getLinks().values()) {
            for (VisualParameters visualParameter : value.getVisualParameters()) {
                if (visualParameter.getShape() instanceof Mesh mesh) {
                    meshes.put(value.getName(), ObjectLoader.getCOLLADA(mesh.getPath()));
                }
            }
        }

        var dh = chain.toDH();

        return new DrawableChain(meshes, dh);
    }

    private DrawableChainLoader() {
    }
}
