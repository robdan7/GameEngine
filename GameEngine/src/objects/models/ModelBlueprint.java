package objects.models;

import java.io.IOException;

import org.lwjgl.opengl.GL20;

import utils.fileSystem.OBJLoader;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.other.Texture;
import utils.rendering.RenderObject;
import utils.rendering.Shaders;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL11.*;

public  class ModelBlueprint implements RenderObject{

	int[] model;
	Vector3f position;
	private Texture texture;
	
	int shader;
	int depthshader;
	
	public ModelBlueprint(String modelFile) {
		this.position = new Vector3f();
		try {
			Model m = OBJLoader.loadTexturedModel(modelFile, 1);
			model = OBJLoader.createVBO(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	}
	
	public void bindTexture(Shaders shader) {
		if (texture == null) {
			System.err.println("This model does not have a texture");
		} else {
			this.texture.bindAsUniform(shader.getShaderProgram());
		}
	}
	
	public void translate(Vector3f v) {
		this.position = v.copy();
	}
	
	public void move(Vector3f deltaV) {
		this.position.add(deltaV);
	}
	
	@Override
	public void discard() {
		this.texture.cleanup();
		if(model.length != 0) {
			OBJLoader.deleteVBO(model[0]);
		}
		/*if (texture != null) {
			texture.cleanup();
		}*/
	}
	
	/**
	 * Render one instance of an object.
	 * 
	 * One vertex and normal contains 3 floats each and one texture coordinate contains 2 floats.
	 * The total length is 3 + 3 + 2 = 8 floats between two vertices. The normal starts at 3 floats and the texture starts at 6 floats.
	 */
	public void render() {
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
	@Override
	public void renderTextured(Matrix4f mat, int shader) {
		if(this.texture == null) {
			System.err.println("Model does not have a texture");
		} else {
			//this.texture.bind();
			this.render(mat, shader);
			//this.texture.unbind();
		}
	}
	
	/**
	 * Render one instance of an object.
	 * 
	 * One vertex and normal contains 3 floats each and one texture coordinate contains 2 floats.
	 * The total length is 3 + 3 + 2 = 8 floats between two vertices. The normal starts at 3 floats and the texture starts at 6 floats.
	 */
	public void renderTextured() {
		if(this.texture == null) {
			System.err.println("Model does not have a texture");
		} else {
			//this.texture.bind();
			this.render();
			//this.texture.unbind();
		}
	}
	
	public void render(Matrix4f mat, int shader) {
		mat.setIdentity();
		mat.translate(this.position);
		mat.createUniform(shader);
		this.bind();
		//glVertexPointer(3, GL_FLOAT, Float.BYTES*8, 0);
		GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*8, 0);
		GL20.glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*3);
		GL20.glVertexAttribPointer(2, 2, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*6);
		//glNormalPointer(GL_FLOAT, Float.BYTES*8,Float.BYTES*3);
		//glTexCoordPointer(2, GL_FLOAT, Float.BYTES*8, Float.BYTES*6);
		glDrawArrays(GL_TRIANGLES, 0, model[1]);
		this.unbind();
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, this.model[0]);
	}
	
	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void setShader(Shaders shader) {
		// TODO Auto-generated method stub
		this.shader = shader.getShaderProgram();
	}

	@Override
	public int getShader() {
		// TODO Auto-generated method stub
		return this.shader;
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		this.depthshader = shader.getShaderProgram();
	}

	@Override
	public int getDepthShader() {
		// TODO Auto-generated method stub
		return this.depthshader;
	}
}
