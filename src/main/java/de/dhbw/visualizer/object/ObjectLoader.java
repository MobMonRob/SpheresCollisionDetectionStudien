package de.dhbw.visualizer.object;

import de.dhbw.visualizer.object.mesh.MeshPart;
import de.dhbw.visualizer.object.mesh.MeshVBO;
import org.jogamp.vecmath.Vector4f;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.EuclidPart;
import org.jzy3d.plot3d.primitives.EuclidVBO2;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.assimp.Assimp.*;

/**
 * An object Loader using Assimp through lwjgl.
 * <p>
 * Can load COLLADA and object files for now.
 *
 * @author Dominik Scharnagl
 */
public class ObjectLoader {

    private static final Logger LOGGER = Logger.getLogger(ObjectLoader.class.getSimpleName());

    private ObjectLoader() {
    }

    /**
     * Add a COLLADA (.dae) File Object to the Scene.
     *
     * @param path the path to the COLLADA File
     */
    public static MeshPart getCOLLADA(String path) {
        var objects = getParts(path);
        for (MeshVBO o : objects) {
            o.setWireframeDisplayed(false);
        }
        //Combine Objects into one composite
        return new MeshPart(objects);
    }

    /**
     * @throws ObjectLoadException if the path is not found
     */
    private static List<MeshVBO> getParts(String path) {
        //Load COLLADA files (creates a separate thread)
        LOGGER.info(() -> "path=" + path);
        // getResource() == null bei Ausführung in Euclid3dView
        URL url = ObjectLoader.class.getResource(path);
        if (url != null) {
            String completePath = url.getPath();
            LOGGER.info(() -> "complete path=" + completePath);
        } else {
            LOGGER.warning("getResource()==null");
        }
        String filePath = ResourceManager.extract(path);
        LOGGER.info(() -> "filePath=" + filePath);

        try (AIScene aiScene = aiImportFile(filePath, 0)) {
            if (aiScene == null) {
                throw new ObjectLoadException("getParts() failed for path \"" + path + "\"!");
            }

            //process Materials
            int numMaterials = aiScene.mNumMaterials();
            PointerBuffer aiMaterials = aiScene.mMaterials();
            List<Material> materials = new ArrayList<>();

            if (aiMaterials != null) {
                for (int i = 0; i < numMaterials; i++) {
                    try (AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i))) {
                        processMaterial(aiMaterial, materials);
                    }
                }
            }

            //Get the Meshes from the File
            PointerBuffer aiMeshes = aiScene.mMeshes();
            AIMesh[] meshes = new AIMesh[aiScene.mNumMeshes()];
            List<MeshVBO> objects = new ArrayList<>();
            //Make objects from the vertices from the
            if (aiMeshes != null) {
                for (int i = 0; i < aiScene.mNumMeshes(); i++) {
                    try (var mesh = AIMesh.create(aiMeshes.get(i))) {
                        meshes[i] = mesh;
                        if (checkIfNotSkeletonBox(meshes[i].mName().dataString())) {
                            List<Float> vertices = new ArrayList<>();
                            processVertices(meshes[i], vertices);
                            objects.add(getVBOObject(vertices, materials.get(meshes[i].mMaterialIndex())));
                        }
                    }
                }
            }

            return objects;
        } catch (Exception ex) {
            throw new ObjectLoadException(ex);
        }
    }

    /**
     * Don't add Boxes to the mesh
     *
     * @param string the input string
     * @return if the input string is Box01 or Box02 or nothing
     */
    private static boolean checkIfNotSkeletonBox(String string) {
        return switch (string) {
            case "Box01", "Box02", "LeftToe", "RightToe" -> false;
            default -> true;
        };
    }

    /**
     * Get the object from the vertices
     *
     * @param vertices the vertices
     * @param material the Material of the object
     * @return the combined object
     */
    private static MeshVBO getVBOObject(List<Float> vertices, Material material) {
        //translate the Floats to an array
        float[] verticesFloat = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesFloat[i] = vertices.get(i);
        }

        //set up and return the object
        MeshVBO vbo = new MeshVBO(verticesFloat, 3);
        vbo.setMaterialAmbiantReflection(new Color(material.ambient().x, material.ambient().y, material.ambient().z));
        vbo.setMaterialDiffuseReflection(new Color(material.diffuse().x, material.diffuse().y, material.diffuse().z));
        vbo.setMaterialSpecularReflection(new Color(material.specular().x, material.specular().y, material.specular().z));
        Color color = new Color((material.ambient().x + material.diffuse().x + material.specular().x) * 1 / 3,
                (material.ambient().y + material.diffuse().y + material.specular().y) * 1 / 3,
                (material.ambient().z + material.diffuse().z + material.specular().z) * 1 / 3, 1.0f);
        vbo.setColor(color);
        vbo.setPolygonWireframeDepthTrick(false);
        return vbo;
    }

    /**
     * Process the vertices from an aim#Mesh to get float value of the vertices
     *
     * @param aiMesh   the aiMesh with the vertices
     * @param vertices the list where the vertices will be stored
     */
    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    /**
     * Process the Materials in an assimp loaded object
     *
     * @param aiMaterial the aiMaterial from the assimp object
     * @param materials  the list where the materials will be added
     */
    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials) {
        AIColor4D colour = AIColor4D.create();

        //set ambient value of the material
        Vector4f ambient = new Vector4f(0, 0, 0, 0);
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //set the diffuse value of the material
        Vector4f diffuse = new Vector4f(0, 0, 0, 0);
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //set the specular value of the material
        Vector4f specular = new Vector4f(0, 0, 0, 0);
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //combine the values
        Material material = new Material(ambient, diffuse, specular, 1.0f);
        materials.add(material);
    }
}
