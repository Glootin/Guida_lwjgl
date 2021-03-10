import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Mesh {
    private static class Face {
        public ArrayList<Integer> vertices = new ArrayList<>();
        public ArrayList<Integer> texture = new ArrayList<>();
        public ArrayList<Integer> normals = new ArrayList<>();

        public Face(String[] values) {
            for (int i = 1; i < values.length; i++) {
                var value = values[i];
                var tmp = value.split("/");
                vertices.add(Integer.parseInt(tmp[0]));
                if(!tmp[1].trim().isEmpty()) texture.add(Integer.parseInt(tmp[1]));
                normals.add(Integer.parseInt(tmp[2]));
            }

        }
    }

    private final ArrayList<Vector3f> vertices = new ArrayList<>();
    private final ArrayList<Vector3f> normals = new ArrayList<>();
    private final ArrayList<Face> faces = new ArrayList<>();
    float posX = 0, posY = 0, posZ = 0, rotX = 0, rotY = 0, rotZ = 0, scale = 1;

    private Mesh() {
    }

    private static Vector3f parseToVectorF(String[] values) {
        return new Vector3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
    }

    private void loadMesh(String filename) throws FileNotFoundException {

        File file = new File(filename);
        Scanner reader = new Scanner(file);
        while (reader.hasNext()) {
            String line = reader.nextLine();
            String[] values = line.split("\\s+");
            switch (values[0]) {
                case "v" -> vertices.add(parseToVectorF(values));
                case "vn" -> normals.add(parseToVectorF(values));
                case "f" -> faces.add(new Face(values));
            }
        }
    }

    public void render() {
        Matrix4f matr = new Matrix4f();
        //applico le trasformate e poi moltiplico ogni vertice per la matrice
        matr.identity().translate(posX, posY, posZ)
                .rotateX((float) Math.toRadians(rotX))
                .rotateY((float) Math.toRadians(rotY))
                .rotateZ((float) Math.toRadians(rotZ))
                .scale(scale);

        faces.forEach(f -> {
            for (Integer i : f.vertices) {
                if (i == null) return;
                var vec = vertices.get(i - 1);
                var ver = new Vector4f(vec.x, vec.y, vec.z, 1);
                ver.mul(matr);
                glVertex3f(ver.x, ver.y, ver.z);
                var norm = new Vector4f(normals.get(i - 1), 1);
                norm.mul(matr);
                glNormal3f(norm.x, norm.y, norm.z);
            }
        });
    }

    public static Mesh generateMesh(String filename) throws FileNotFoundException {
        Mesh mesh = new Mesh();
        mesh.loadMesh(filename);
        return mesh;
    }
}
