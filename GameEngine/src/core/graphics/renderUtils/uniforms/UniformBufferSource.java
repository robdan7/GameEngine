package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import core.graphics.renderUtils.uniforms.UniformBufferObject.glVariableType;
import core.utils.math.*;
import core.utils.other.BufferTools;

/**
 * This class represents one variable inside a uniform buffer object.
 * @author Robin
 *
 */
public class UniformBufferSource {
	
	private int offset;		// offset in bytes.
	private String name;
	private glVariableType type;
	private UniformBufferObject object;
	private FloatBuffer buffer;

	/**
	 * 
	 * @param name - The name of the individual uniform.
	 * @param type
	 */
	public UniformBufferSource(String name, glVariableType type) {
		this.name = name;
		this.type = type;
		this.buffer = BufferUtils.createFloatBuffer(type.getStride());
	}
	

	public void updateSource(Vector4f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector3f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector2f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.object.updateSource(this);
	}
	
	public void updateSource(float... values) {
		BufferTools.putInBuffer(this.buffer, 0, values);
		this.object.updateSource(this);
	}
	
	public void updateSource(Matrix4f mat) {
		BufferTools.putInBuffer(this.buffer, 0, mat);

		this.object.updateSource(this);
	}
	 
	/**
	 * Bind this buffer source to a buffer object.
	 */
	public void bindToBufferObject(UniformBufferObject o) {
		//this.object = o;
		this.object = o;
		o.bindBufferSource(this);
	}
	
	FloatBuffer getBuffer() {
		return this.buffer;
	}
	
	public int  getStride() {
		return this.getType().getStride();
	}

	/**
	 * Return the buffer offset in machine units, not bytes.
	 * @return
	 */
	public int getOffset() {
		return this.offset;
	}
	
	/**
	 * Set the offset of this source.
	 * @param offset - offset in machine units.
	 */
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	String getName() {
		return this.name;
	}
	
	glVariableType getType() {
		return this.type;
	}
}
