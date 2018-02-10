package objects.lights;

import static org.lwjgl.opengl.GL15.*;


import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import utils.math.Vector4f;
import utils.rendering.Uniform;


public abstract class Light {
	int lightNumber;
	int UBO;
	int index;

	FloatBuffer uniformBuffer;
	
	/**
	 * 
	 * @param position - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public Light (Vector4f position, Vector4f diffuse, Vector4f ambient, int index) {
		uniformBuffer = BufferUtils.createFloatBuffer(12);
		UBO = glGenBuffers();
		genUniformBuffer(new float[][] {position.asFloat(), ambient.asFloat(), diffuse.asFloat()});
		this.index = index;
		createUniform();
	}
	
	void createUniform() {
		Uniform.createUniformBlock(UBO, uniformBuffer, index, GL_DYNAMIC_DRAW);
	}
	
	public void updateUniform() {
		Uniform.updateUniformBlock(UBO, this.uniformBuffer);
	}
	
	public abstract void setPosition(float x, float y, float z, float w);
	
	public abstract void setPosition(Vector4f position);
	
	void genUniformBuffer(float[]... values) {
		for (int i = 0; i < values.length; i++) {
			this.uniformBuffer.put(values[i]);
		}
		this.uniformBuffer.flip();
	}
	
	public abstract Vector4f getPosition();
}
