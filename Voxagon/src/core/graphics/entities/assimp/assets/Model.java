package core.graphics.entities.assimp.assets;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import core.graphics.entities.assimp.AssimpScene;
import core.graphics.renderUtils.VertexAttribute;
import core.utils.datatypes.GlueList;
import core.utils.datatypes.buffers.FloatBufferPartition;
import core.utils.datatypes.buffers.pointers.FloatBufferPointer;
import core.utils.other.BufferTools;

public class Model {
	private MeshContainer[] meshes;
	private int[] meshVAO;
	private int instanceVBO;
	private int instanceStorageSize, occupiedInstances, totalInstances;
	private String name;
	private FloatBuffer instanceData;
	private FloatBufferPointer instanceDataPointer;
	
	/*
	 * TODO The model instances should have some form of system to add instance 
	 * variables to the model. I should also be possible to define custom buffer sizes for the models
	 * and a way to add custom shaders.
	 * 
	 * One way to do it would be to have one material per file. 
	 */

	/**
	 * 
	 * @param name - The name tag of the model.
	 * @param instanceCount - Max number of instances for this model.
	 * @param instanceStorageSize - The buffer size for each instance. Measured in floats.
	 * @param meshes - Mesh containers for every mesh associated with this model.
	 */
	public Model(String name, int instanceCount, int instanceStorageSize, MeshContainer... meshes) {
		this.totalInstances = instanceCount;
		this.instanceData = BufferUtils.createFloatBuffer(instanceCount*instanceStorageSize);
		this.meshes = meshes;
		this.instanceVBO = BufferTools.createVBO(this.instanceData, GL15.GL_STATIC_DRAW);
		VertexAttribute attribute = new VertexAttribute(3,4,GL15.GL_FLOAT, false, 0, 0, 1);
		
		this.meshVAO = new int[this.meshes.length];
		
		for (int i = 0; i < this.meshVAO.length; i++) {
			this.meshVAO[i] = GL30.glGenVertexArrays();
			
			/* bind the individual mesh to the vao*/
			meshes[i].bindToVAO(this.meshVAO[i]);
			
			/* */
			BufferTools.addVAOattributes(this.meshVAO[i], this.instanceVBO, attribute);
			
		}
		this.instanceStorageSize = instanceStorageSize;
	}
	
	/**
	 * Load a model from a complete scene file. All the meshes in the scene will be considered as 
	 * part of the model.
	 * @param file
	 */
	@Deprecated
	public Model(String name, int instanceStorageSize, String file) {
		AssimpScene scene = new AssimpScene(file);
		this.meshes = scene.getMeshes();
		this.instanceData = BufferUtils.createFloatBuffer(0);
		this.instanceVBO = BufferTools.createVBO(this.instanceData, GL15.GL_STATIC_DRAW);
		VertexAttribute attribute = new VertexAttribute(3,4,GL15.GL_FLOAT, false, 0, 0, 1);
		
		this.meshVAO = new int[this.meshes.length];
		
		for (int i = 0; i < this.meshVAO.length; i++) {
			this.meshVAO[i] = GL30.glGenVertexArrays();
			
			/* bind the individual mesh to the vao*/
			meshes[i].bindToVAO(this.meshVAO[i]);
			
			/* */
			BufferTools.addVAOattributes(this.meshVAO[i], this.instanceVBO, attribute);
			
		}
		this.instanceStorageSize = instanceStorageSize;
		
		this.instanceDataPointer = new FloatBufferPointer(this.instanceData);
		scene.dispose();
	}
	
	/**
	 * Create a model without any instances.
	 * @param name
	 * @param instanceStorageSize
	 * @param containers
	 */
	public Model(String name, int instanceStorageSize, MeshContainer...containers) {
		this(name,0,instanceStorageSize, containers);
		this.instanceDataPointer = new FloatBufferPointer(this.instanceData);
	}
	
	/**
	 * Allocate more space in the instance buffer.
	 * @param instances
	 */
	public void allocateInstances(int instances) {
		if (this.instanceDataPointer == null) {
			throw new UnsupportedOperationException("The buffer space of this model is fixed to " + this.totalInstances + " instances");
		}
		this.totalInstances += instances;
		
		/* Create a new buffer and change the pointer to it. The current instances won't know it happened.*/
		this.instanceData = BufferTools.resizedBufferCopy(this.instanceData, this.totalInstances*this.instanceStorageSize);
		this.instanceDataPointer.changeBuffer(this.instanceData);
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
		
		
		/* Create a partition with a pointer or buffer depending on if this model support dynamic buffer sizes.*/
		FloatBufferPartition result = null;
		if (this.instanceDataPointer != null) {
			result = new FloatBufferPartition(this.instanceDataPointer, startPosition, startPosition+this.instanceStorageSize);
		} else {
			result = new FloatBufferPartition(instanceData, startPosition, startPosition+this.instanceStorageSize);
		}
		
		return  result;
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
