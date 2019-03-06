package core.graphics.lights;

import static org.lwjgl.opengl.GL15.*;


import java.nio.FloatBuffer;

import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.graphics.renderUtils.uniforms.*;
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
	 * 
	 * @param vector - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	@Deprecated
	protected Light (Vector4f vector, Vector4f diffuse, Vector4f ambient, Vector4f specular, String uniformFile) {
		super(UniformBufferObject.glVariableType.VEC4, "", "");
		//super(16);

		//lightUniform = new UniformObject(uniformFile, GL_STATIC_DRAW);
		//super.bindToUniformObject(lightUniform);
		//super.updateUniform(BufferTools.asFlippedFloatBuffer(this.to1dArray(new float[][] {vector.asFloats(), ambient.asFloats(), diffuse.asFloats(), specular.asFloats()})));
	}
	
	/**
	 * Create a custom light with several properties.
	 * @param uniformName - The uniform block name used in GLSL.
	 * @param lightProperties
	 */
	protected Light(String uniformName, String[] names, Vector4f... lightProperties) {
		super(UniformBufferObject.glVariableType.VEC4, names);
		//super(lightProperties.length*Float.BYTES);
		this.lightProperties = lightProperties;
		this.lightUniform = new UniformBufferObject(uniformName, GL_STATIC_DRAW);
		
		//super.bindToUniformObject(this.lightUniform);
		super.bindToBufferObject(this.lightUniform);
		
		try {
			this.lightUniform.finalizeBuffer();
		} catch (ShaderCompileException e) {
			e.printStackTrace();
		}
		super.updateSource(0, lightProperties);
		//super.updateUniform(BufferTools.asFlippedFloatBuffer(Vector.to4fArray(this.lightProperties)));
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
