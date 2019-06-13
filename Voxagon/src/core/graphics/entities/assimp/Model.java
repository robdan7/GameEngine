package core.graphics.entities.assimp;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;
import org.lwjgl.opengl.GL15;

import core.utils.datatypes.GlueList;
import core.utils.datatypes.buffers.FloatBufferPartition;
import core.utils.other.BufferTools;

public class Model {
	private GlueList<MeshContainer> meshes;
	private GlueList<Integer> meshVAO;
	private int instanceVBO;
	private int instanceStorageSize, occupiedInstances;
	private String name;
	private FloatBuffer instanceData;

	/**
	 * 
	 * @param name - The name tag of the model.
	 * @param instanceCount - Max number of instances for this model.
	 * @param instanceStorageSize - The buffer size for each instance. Measured in floats.
	 * @param meshes - Mesh containers for every mesh associated with this model.
	 */
	public Model(String name, int instanceCount, int instanceStorageSize, MeshContainer... meshes) {
		this.instanceData = BufferUtils.createFloatBuffer(instanceCount*instanceStorageSize);
		this.meshes = new GlueList<MeshContainer>();
		/*
		 * TODO add this line.
		 */
		//this.instanceVBO = BufferTools.createVBO(this.instanceData, GL15.GL_STATIC_DRAW);
		this.instanceStorageSize = instanceStorageSize;
	}
	
	public void addMesh(MeshContainer mesh) {
		this.meshes.add(mesh);
	}
	
	/**
	 * Request a buffer partition for an instance of this model. It is up to the user to not request more instances 
	 * than the model has to offer. The maximum set of instances is set during creation of the model, and cannot 
	 * change during its lifespan.
	 * @return the buffer partition for which the instance is allowed to store any data
	 * associated with the instance.
	 */
	public FloatBufferPartition requestInstanceBuffer() {
		
		/*
		 * TODO add this line so it works. Or maybe not, this seems wrong.
		 */
		//BufferTools.revalidateVertexBuffer(GL15.GL_ARRAY_BUFFER, this.instanceVBO, this.instanceData, GL15.GL_STATIC_DRAW);
		
		
		
		/* Just create a buffer partition and return it.*/
		int startPosition = this.occupiedInstances*this.instanceStorageSize;
		this.occupiedInstances ++;
		return  new FloatBufferPartition(instanceData, startPosition, startPosition+this.instanceStorageSize);
	}
	
	/**
	 * Create a model instance and return it. It is up to the user to fill 
	 * this instance with whatever is needed for rendering.
	 * @return
	 */
	public ModelInstance getInstance() {
		return new ModelInstance(this.requestInstanceBuffer());
	}
	
	/**
	 * Get the name of this model. The name of a model must be unique among other models
	 * in the same scene or group of models.
	 * @return
	 */
	public String getName() {
		return this.name;
	}
}
