package core.demo;
import java.io.File;

import org.lwjgl.*;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AILight;

import core.graphics.entities.assimp.MeshContainer;
import core.utils.fileSystem.FileManager;

public class AssimpMeshTest {

	public AssimpMeshTest(File file) {
		AIScene scene = Assimp.aiImportFile(file.toString(),
				Assimp.aiProcess_Triangulate |
				Assimp.aiProcess_JoinIdenticalVertices);
		if (scene == null) {
			System.err.println("Assimp scene was not created.");
			System.exit(0);
		}
		
		//MeshContainer mesh = new MeshContainer(1,scene);

		
		
		System.out.println("Meshes: ");
		for (int i = 0; i < scene.mNumMeshes(); i++) {
			AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
			System.out.println(mesh.mName().dataString());
			for (int l = 0; l < mesh.mNumVertices(); l++) {
				AIVector3D vertex = mesh.mVertices().get(l);
				System.out.println(vertex.x() + ", " + vertex.y() + ", " + vertex.z());
				vertex = mesh.mNormals().get(l);
				System.out.println(vertex.x() + ", " + vertex.y() + ", " + vertex.z());
			}
			System.out.println(mesh.mNumVertices());
		}
		
		System.out.println("\n Lights: ");
		for (int i = 0 ; i < scene.mNumLights(); i++) {
			AILight light = AILight.create(scene.mLights().get(i));
			System.out.println(light.mName().dataString());
		}
		System.out.println("\n Nodes: ");
		for (int i = 0; i < scene.mRootNode().mNumChildren(); i++) {
			AINode node = AINode.create(scene.mRootNode().mChildren().get(i));
			System.out.println("Node name: " + node.mName().dataString());
			System.out.println("Meshes: " + node.mNumMeshes());
			for (int j = 0; j < node.mNumMeshes(); j++) {
				System.out.println(AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i))).mName().dataString());
			}
			System.out.println("Children: " + node.mNumChildren());
			if (node.mNumChildren() > 0) {
				for (int j = 0;  j < node.mNumChildren(); j++) {
					AINode nod = AINode.create(node.mChildren().get());
					System.out.println("Child name: " + nod.mName().dataString());
				}
			}
			
			System.out.println();
	
		}
	
		scene.free();
	}
	
	public static void main(String[] args) {
		File file = FileManager.getFile("/Assets/triangles.dae");
		new AssimpMeshTest(file);
	}

}
