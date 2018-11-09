package core.graphics.lights;

import static org.lwjgl.opengl.GL15.*;


import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import core.graphics.renderUtils.uniforms.UniformObject;
import core.graphics.renderUtils.uniforms.UniformSource;
import core.graphics.renderUtils.uniforms.UniformTools;
import core.utils.math.Vector;
import core.utils.math.Vector4f;
import core.utils.other.BufferTools;
import core.utils.other.Pair;


public abstract class Light extends UniformSource {
	private int lightNumber;
	private UniformObject uniform;

	private FloatBuffer uniformBuffer;
	
	/**
	 * 
	 * @param vector - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public Light (Vector vector, Vector diffuse, Vector ambient, Vector specular, int index, String uniformFile) {
		super(16);
		//super.updateBuffer(this.to1dArray(new float[][] {vector.asFloat(), ambient.asFloat(), diffuse.asFloat()}));
		
		uniform = new UniformObject(uniformFile, GL_STATIC_DRAW);
		super.bindToUniformObject(uniform);
		super.updateUniform(BufferTools.asFlippedFloatBuffer(this.to1dArray(new float[][] {vector.asFloat(), ambient.asFloat(), diffuse.asFloat(), specular.asFloat()})));
		/*this.uniform = new UniformObject(index);

		float[] bufferData = to1dArray(new float[][] {vector.asFloat(), ambient.asFloat(), diffuse.asFloat()});
		Pair<FloatBuffer, Integer> data = UniformTools.createUniformBlock(bufferData, index, GL_DYNAMIC_DRAW);
		this.UBO = data.getSecond();
		this.uniformBuffer = data.getFirst();*/
	}
	
	/*void createUniform() {
		UniformTools.createUniformBlock(UBO, uniformBuffer, index, GL_DYNAMIC_DRAW);
	}*/
	
	public void updateUniform(float[] data) {
		/*this.uniformBuffer = BufferTools.asFloatBuffer(data);
		this.uniformBuffer.flip();
		UniformTools.updateUniformBlock(UBO, this.uniformBuffer,0);*/
		super.updateUniform(data);
	}
	
	/**
	 * Set the position of this light and update the associated uniform block.
	 * @param x - x coordinate.
	 * @param y - y coordinate.
	 * @param z - z coordinate.
	 * @param w - w coordinate.
	 */
	public abstract void setPosition(float x, float y, float z, float w);
	
	/**
	 * Set the position of this light and update the associated uniform block.
	 * @param position - 
	 */
	public abstract void setPosition(Vector4f position);
	
	protected float[] to1dArray(float[][] values) {
		int length = 0;
		for(float[] a : values) {
			length += a.length;
		}
		float[] result = new float[length];
		int i = 0;
		for(float[] a : values) {
			for (float f : a) {
				result[i] = f;
				i ++;
			}
		}
		return result;
	}
	
	protected final FloatBuffer getFloatBuffer() {
		return this.uniformBuffer;
	}
	
	public abstract Vector getPosition();
}
