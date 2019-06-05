package core.graphics.entities.assimp;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL15;

import core.graphics.renderUtils.VertexAttribute;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.other.BufferTools;

/**
 * <p>This represents a single mesh with its own vertex buffer. A mesh is just a group of
 * vertices with the same material (shader). A model with several materials should also 
 * have several meshes in it.</p>
 * 
 * @author Robin
 *
 */
public class MeshContainer {
	public FloatBuffer vertexBuffer;	// Vertex buffer
	private IntBuffer indexBuffer;
	private AIMesh mesh;
	private int VBO, IBO;	// Vertex buffer and index buffer objects.
	
	@Deprecated
	private int VAO;
	private VertexAttribute[] vertexAttributes;
	
	private boolean hasNormals = false, hasTextureCoords = false;
	

	/**
	 * Create a new mesh container with Assimp. Only one instance should exist 
	 * of the same mesh. Use instances for rendering.
	 * @param mesh - The mesh object provided by Assimp. This is not a complete representation 
	 * of a model mesh.
	 */
	public MeshContainer(AIMesh mesh) {
		
		this.hasNormals = mesh.mNormals().capacity() > 0;
		this.hasTextureCoords = mesh.mTextureCoords().capacity() > 0;
		
		this.compileMeshBuffer(mesh);
		this.compileIndexBuffer(mesh);
		
		this.createGLBuffers();
		this.mesh = mesh;
	}
	
	/**
	 * Create the VBO which stores all the (unique) vertices in the mesh.
	 * @param mesh
	 */
	private void compileMeshBuffer(AIMesh mesh) {		
		int vertexCapacity = mesh.mNumVertices()*(Vector3f.SIZE+(this.hasNormals?1:0)*Vector3f.SIZE + (this.hasTextureCoords?1:0)*Vector2f.SIZE);
		this.vertexBuffer = BufferUtils.createFloatBuffer(vertexCapacity);
		
		AIVector3D vector = null;
		AIVector3D normal = null;
		for (int i = 0 ; i < mesh.mNumVertices(); i++) {
			vector = mesh.mVertices().get(i);
			normal = mesh.mNormals().get(i);
			this.vertexBuffer.put(vector.x());
			this.vertexBuffer.put(vector.y());
			this.vertexBuffer.put(vector.z());
			this.vertexBuffer.put(normal.x());
			this.vertexBuffer.put(normal.y());
			this.vertexBuffer.put(normal.z());
			this.vertexBuffer.put(new float[2]);
		}
		this.vertexBuffer.flip();
		
	}
	
	/**
	 * Create the IBO which stores vertex indices for every face.
	 * @param mesh
	 */
	private void compileIndexBuffer(AIMesh mesh) {
		int indexCapacity = mesh.mNumFaces();
		//this.indexBuffer = BufferUtils.createIntBuffer(indexCapacity*Vector3f.SIZE);
		this.indexBuffer = BufferUtils.createIntBuffer(indexCapacity*Vector3f.SIZE);	// faces * indices per face.
		AIFace face = null;
		for (int i = 0; i < mesh.mNumFaces(); i++) {
			face = mesh.mFaces().get(i);
			this.indexBuffer.put(face.mIndices().get(0));
			this.indexBuffer.put(face.mIndices().get(1));
			this.indexBuffer.put(face.mIndices().get(2));
			
		}
		this.indexBuffer.flip();
	}
	
	/**
	 * Create the VBO, IBO and VAO associated with this mesh. Use a separate VBO for instanced rendering.
	 */
	@Deprecated
	private void generateGLBuffers() {
		this.vertexAttributes = new VertexAttribute[3];
		this.vertexAttributes[0] = new VertexAttribute(0, 3, GL_FLOAT, false, Float.BYTES * 8, 0);

		if (this.hasNormals()) {
			this.vertexAttributes[1] = new VertexAttribute(1, 3, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 3);
		}

		if (this.hasTextureCoords()) {
			this.vertexAttributes[2] = new VertexAttribute(2, 2, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 6);
		}
		this.VBO = BufferTools.createVBO(this.vertexBuffer, GL15.GL_STATIC_DRAW);
		this.IBO = BufferTools.createIBO(this.indexBuffer, GL15.GL_STATIC_DRAW);
		
		this.VAO = BufferTools.createVAO(this.VBO, this.vertexAttributes);
	}
	
	/**
	 * Create the OpenGL buffer objects associated with this mesh. The buffer objects 
	 * contains all vertices and every face of the mesh, as well as vertex attributes.
	 */
	private void createGLBuffers() {
		this.vertexAttributes = new VertexAttribute[3];
		this.vertexAttributes[0] = new VertexAttribute(0, 3, GL_FLOAT, false, Float.BYTES * 8, 0);

		if (this.hasNormals()) {
			this.vertexAttributes[1] = new VertexAttribute(1, 3, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 3);
		}

		if (this.hasTextureCoords()) {
			this.vertexAttributes[2] = new VertexAttribute(2, 2, GL_FLOAT, false, Float.BYTES * 8, Float.BYTES * 6);
		}
		this.VBO = BufferTools.createVBO(this.vertexBuffer, GL15.GL_STATIC_DRAW);
		this.IBO = BufferTools.createIBO(this.indexBuffer, GL15.GL_STATIC_DRAW);
	}
	
	/**
	 * Bind this mesh to a VAO. Make sure to store the instances in a separate buffer object, 
	 * as the buffer created by the mesh is reserved to vertices.
	 * @param VAO
	 */
	public void bindToVAO(int VAO) {
		//BufferTools.addVAOattributes(VAO, this.VBO, this.vertexAttributes);
		/*
		 * TODO Add the VBO and IBO to the VAO with proper connection between the two objects.
		 */
	}
	
	private boolean hasNormals() {
		return this.hasNormals;
	}
	
	private boolean hasTextureCoords() {
		return this.hasTextureCoords;
	}

}
