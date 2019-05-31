package core.demo;
import java.io.File;

import org.lwjgl.*;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import core.utils.fileSystem.FileManager;

public class AssimpMesh {

	public AssimpMesh(File file) {
		AIScene scene = Assimp.aiImportFile(file.toString(),
				Assimp.aiProcess_Triangulate);
		if (scene == null) {
			System.err.println("Assimp scene was not created.");
			System.exit(0);
		}
		
		AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
		for (int i = 0; i < scene.mMeshes().capacity(); i++) {
			//System.out.println(AIMesh.create(scene.mMeshes().get(i)).);
		}
		
		
		for (int i = 0; i < mesh.mVertices().capacity(); i++) {
			AIVector3D vertex = mesh.mVertices().get(i);
			System.out.println(vertex.x() + ", " + vertex.y() + ", " + vertex.z());
			
		}
		scene.free();
	}
	
	public static void main(String[] args) {
		File file = FileManager.getFile("/Assets/untitled.dae");
		new AssimpMesh(file);
	}

}
