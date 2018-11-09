package core.graphics.models;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.uniforms.UniformSource;
import core.utils.fileSystem.*;
import core.utils.math.Matrix4f;
import core.utils.math.Vector;
import core.utils.math.Vector3f;
import core.utils.other.Texture;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;

public  class ModelBlueprint implements RenderObject{

	private int[] model;
	private Vector3f position;
	private Texture texture;
	private UniformSource transformUniform;
	private Matrix4f transformMatrix;
	private FloatBuffer transformBuffer;
	
	private Shaders shader;
	private int depthshader;
	
	public ModelBlueprint(String modelFile) {
		this.position = new Vector3f();
		try {
			Model m = OBJLoader.loadTexturedModel(modelFile, 1);
			model = OBJLoader.createVBO(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.transformMatrix = new Matrix4f();
		this.transformBuffer = BufferUtils.createFloatBuffer(Matrix4f.getSize());
		this.transformMatrix.put(this.transformBuffer); // Initiate the buffer with the identity matrix.
		//this.transformBuffer.flip();
	}
	
	public ModelBlueprint(String modelFile, String textureUniformName) {
		this.position = new Vector3f();
		try {
			Model m = OBJLoader.loadTexturedModel(modelFile, 1);
			model = OBJLoader.createVBO(m);
			this.texture = new Texture(m.getTexture(), textureUniformName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.transformMatrix = new Matrix4f();
		this.transformBuffer = BufferUtils.createFloatBuffer(Matrix4f.getSize());
		this.transformMatrix.put(this.transformBuffer); // Initiate the buffer with the identity matrix.
		//this.transformBuffer.flip();
	}
	
	/**
	 * bind the model texture to a shader. The shader must be bound first.
	 * @param shader
	 */
	public void bindTexture(Shaders shader) {
		if (texture == null) {
			System.err.println("This model does not have a texture");
		} else {
			this.texture.bindAsUniform(shader);
		}
	}
	
	/**
	 * move this model in world space.
	 * @param v
	 */
	public void translate(Vector3f v) {
		this.position.x = v.x;
		this.position.y = v.y;
		this.position.z = v.z;
		this.transformMatrix.translate(this.position);
		this.transformMatrix.put(this.transformBuffer);
	}
	
	/*public void move(Vector deltaV) {
		this.position.add(deltaV);
		this.transformMatrix.translate(this.position);
		this.transformMatrix.put(this.transformBuffer);
	}*/
	
	@Override
	public void discard() {
		this.texture.cleanup();
		if(model.length != 0) {
			OBJLoader.deleteVBO(model[0]);
		}
		if (texture != null) {
			texture.cleanup();
		}
	}
	
	/**
	 * Render one instance of an object. This will also update all connected uniforms.
	 */
	public void render() {
		/*FloatBuffer b = BufferUtils.createFloatBuffer(16);
		this.transformMatrix.setIdentity();
		this.transformMatrix.translate(new Vector3f(0,10,0));
		this.transformMatrix.put(b);*/
		this.transformUniform.updateUniform(this.transformBuffer);
		
		this.bind();
		//glVertexPointer(3, GL_FLOAT, Float.BYTES*8, 0);
		//glNormalPointer(GL_FLOAT, Float.BYTES*8,Float.BYTES*3);
		//glTexCoordPointer(2, GL_FLOAT, Float.BYTES*8, Float.BYTES*6);
		GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*8, 0);
		GL20.glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*3);
		GL20.glVertexAttribPointer(2, 2, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*6);
		glDrawArrays(GL_TRIANGLES, 0, model[1]);
		this.unbind();
	}
	
	/**
	 * Render one instance of an object.
	 * 
	 * One vertex and normal contains 3 floats each and one texture coordinate contains 2 floats.
	 * The total length is 3 + 3 + 2 = 8 floats between two vertices. The normal starts at 3 floats and the texture starts at 6 floats.
	 */
	public void renderTextured() {
		if(this.texture == null) {
			throw new RuntimeException("Model does not have a texture");
		} else {
			this.texture.bindAsUniform(shader);
			this.render();
			this.texture.unbind();
		}
	}
	
	/**
	 * Bind this model to a transform uniform.
	 * @param matrixUniform
	 */
	public void bindTransformMatrix(UniformSource matrixUniform) {
		if(matrixUniform.getSize() != Matrix4f.getSize()) {
			throw new IllegalArgumentException("Unform does not contain a 4x4 matrix");
		}
		this.transformUniform = matrixUniform;
	}
	
	private void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, this.model[0]);
	}
	
	private void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void setShader(Shaders shader) {
		this.shader = shader;
	}

	@Override
	public int getShader() {
		return this.shader.getShaderProgram();
	}
	
	public Vector3f getPosition() {
		return this.position;
	}

	@Override
	public void setDepthShader(Shaders shader) {
		this.depthshader = shader.getShaderProgram();
	}

	@Override
	public int getDepthShader() {
		return this.depthshader;
	}
	
	protected Matrix4f getTransformMatrix() {
		return this.transformMatrix;
	}
}
