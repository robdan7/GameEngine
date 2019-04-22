package core.graphics.lights;

import static org.lwjgl.opengl.GL15.*;


import java.nio.FloatBuffer;

import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.graphics.renderUtils.uniforms.*;
import core.graphics.shading.GLSLvariableType;
import core.utils.math.Vector;
import core.utils.math.Vector4f;
import core.utils.other.BufferTools;

/**
 * This class represents a general light object, which is essentially a uniform block 
 * with defined properties inside.
 * @author Robin
 *
 */
public abstract class Light extends UniformBufferSource {
	private UniformBufferObject lightUniform;

	private FloatBuffer uniformBuffer;
	private Vector4f[] lightProperties;
	
	/**
	 * Create a custom light with several properties.
	 * @param uniformName - The uniform block name used in GLSL.
	 * @param lightProperties
	 */
	protected Light(String uniformName, String[] names, Vector4f... lightProperties) {
		super(0, GLSLvariableType.VEC4, names);
		this.lightProperties = lightProperties;
		this.lightUniform = new UniformBufferObject(uniformName);
		super.bindToBufferObject(this.lightUniform, 0);
		super.updateSource(0, this.lightProperties);
	}
	


	public void updateUniform(float[] data) {
		//super.updateUniform(data);
		super.updateSource(0, data);
	}
	
	/**
	 * Store the light position as a shader uniform.
	*/
	public abstract void updatePosition();
	
	
	/**
	 * Set the camera position to a static value.
	 * @param v
	 */
	public abstract void setPosition(Vector4f v);
	
	/**
	 * Set the camera position to a static value.
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public abstract void setPosition(float x, float y, float z, float w);
	
	/**
	 * Bind the light position to a vector.
	 * N.B: Both vectors will be the same.
	 * @param v
	 */
	public abstract void bindPosition(Vector4f v);
	
	/**
	 * Generate a 1d array from a 2d array.
	 * @param values
	 * @return
	 */
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
	
	public abstract Vector4f getPosition();
}
