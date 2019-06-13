package core.graphics.entities.assimp;

import java.io.File;
import java.util.concurrent.Semaphore;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import core.utils.datatypes.GlueList;
import core.utils.fileSystem.FileManager;


public class AssimpScene {
	private AssimpNode root;
	private AIScene scene;

	public AssimpScene(String file) {
		File AssimpFile = FileManager.getFile(file);
		
		scene = Assimp.aiImportFile(AssimpFile.toString(),
				Assimp.aiProcess_Triangulate |
				Assimp.aiProcess_JoinIdenticalVertices);
		if (scene == null) {
			System.err.println("Assimp scene was not created.");
			System.exit(0);
		}
		//System.out.println(scene.mNumMeshes());
		this.root = new AssimpNode(scene);
		System.out.println("The scene was created successfully! Now there will be cake.");
	}

	private class AssimpNode {
		private AINode node;
		private NodeLayer layer;
		private AssimpNode[] children;
		private AssimpNode parent;
		private AIScene scene;
		private boolean isRoot = false;
		private ModelInstance instance;
		/**
		 * Create an Assimp node with the given address.
		 * @param address
		 */
		private AssimpNode(AIScene scene, AssimpNode parent, long address, NodeLayer layer) {
			this.parent = parent;
			this.node = AINode.create(address);
			this.layer = layer;
			this.scene = scene;
			this.createChildren();
			this.createModels();
		}
		
		private void createModels() {
			if (this.node.mNumMeshes() == 0) {
				return;
			}
			int[] meshes = new int[this.node.mNumMeshes()];
			for (int i = 0; i < this.node.mNumMeshes(); i++) {
				meshes[i] = this.node.mMeshes().get(i);
			}
			String name = AIMesh.create(this.scene.mMeshes().get(meshes[0])).mName().dataString();
			
			/*
			 * TODO temporarily remove the GL buffers in the model class or add LWJGL start code.
			 */
			PreModelContainer container = this.layer.requestModelContainer(name, meshes);
			
			
			Thread th = new Thread() {
				public void run() {
					instance = layer.requestModelInstance(container);
				}
			};
			
			th.start();
		}
		
		/**
		 * Use this to create a root node.
		 * @param node
		 */
		private AssimpNode(AIScene scene) {
			this.node = scene.mRootNode();
			this.layer = new NodeLayer();
			this.isRoot = true;
			this.scene = scene;
			this.createChildren();
			this.layer.finalizeLayer();
		}
		
		private void createChildren() {
			this.children = new AssimpNode[this.node.mNumChildren()];
			if (this.children.length == 0) {
				return;
			}
			for (int i = 0; i < this.node.mNumChildren(); i++) {
				this.children[i] = new AssimpNode(this.scene, this, this.node.mChildren().get(i), this.layer.requestNext());
			}
		}
		
		boolean isRoot() {
			return this.isRoot;
		}
	}
	
	/**
	 * <p>
	 * Keeps track of all models of a layer in the node tree. Nodes share the same
	 * model source for instancing if they are siblings in the tree, which is equal
	 * to being on the same level.
	 * </p>
	 * 
	 * @author Robin
	 *
	 */
	private class NodeLayer {
		private NodeLayer next;
		private GlueList<Model> models;
		private GlueList<PreModelContainer> modelQueue;
		private int layerID = 0; /* This is just an identifier for all the layers. Only used for debugging. */

		NodeLayer() {
			this.modelQueue = new GlueList<PreModelContainer>();
			this.models = new GlueList<Model>();
		}

		/**
		 * Request the next layer in the layer chain. A new layer is created if no layer
		 * exists.
		 * 
		 * @return
		 */
		NodeLayer requestNext() {
			if (!this.hasNext()) {
				this.next = new NodeLayer();
				this.next.layerID = this.layerID + 1;
			}

			return this.next;
		}

		boolean hasNext() {
			return this.next != null;
		}

		/**
		 * Request a single model instance in a layer. This method blocks all threads
		 * until the layer is completed.
		 * 
		 * @param container
		 * @return
		 */
		ModelInstance requestModelInstance(PreModelContainer container) {
			try {
				container.nodeSemaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* It's ok to create a model instance now */

			return container.model.getInstance();
		}

		/**
		 * Request a model container used for creating an instance of a model. The
		 * container is not a model, it's just a key used when creating an instance.
		 * 
		 * @param name   - The name of the model.
		 * @param meshes - Addresses of all meshed required to create the model.
		 * @return A model container used when creating a model instance. This is the
		 *         authenticator for {@link #requestModelInstance(PreModelContainer)}.
		 */
		synchronized PreModelContainer requestModelContainer(String name, int... meshes) {
			PreModelContainer container = null;
			for (PreModelContainer m : this.modelQueue) {
				if (m.name.equals(name)) {
					container = m;
					break;
				}
			}
			if (container == null) {
				container = new PreModelContainer(name, meshes);
				this.modelQueue.add(container);
			}
			container.enqueue();
			return container;
		}

		/**
		 * Go through every model container on this layer and create a model for all of
		 * them. Every node that requested an instance will be unlocked. The successor
		 * of this layer will be called directly after this layer is finished. There is
		 * no need to call every layer in the chain.
		 */
		private void finalizeLayer() {

			for (PreModelContainer model : this.modelQueue) {
				model.model = new Model(model.name, model.waitingNodes, 16);
				model.nodeSemaphore.release(model.getQueue());
			}
			this.modelQueue.clear();
			if (this.hasNext()) {
				this.next.finalizeLayer();
			}
		}
	}


	
	/**
	 * <p>This is sort of a container for the data needed to create a model. It keeps 
	 * track of how many instances there should be and the IDs of every mesh 
	 * associated with the model itself.</p>
	 * 
	 * <p>Every model has a fixed buffer space </p>
	 * @author Robin
	 *
	 */
	class PreModelContainer {
		private int waitingNodes = 0;
		private Semaphore nodeSemaphore;
		private int[] meshes;
		private String name;
		
		/* This should be null until a model is created by the layer */
		private Model model;
		
		
		private PreModelContainer(String name, int... meshes) {
			this.meshes = meshes;
			this.name = name;
			this.nodeSemaphore = new Semaphore(0);
		}
		
		/**
		 * Increase the number of waiting nodes.
		 */
		private synchronized void enqueue() {
			this.waitingNodes ++;
		}
		
		/**
		 * 
		 * @return The number of waiting nodes.
		 */
		private synchronized int getQueue() {
			return this.waitingNodes;
		}
	}
}
