package core.graphics.renderUtils;

import java.nio.FloatBuffer;

import core.graphics.renderUtils.UniformBufferObject.glVariableType;
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
	}
	
	public void updateSource () {
		this.buffer.flip();	// Make it readable to OpenGL.
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector4f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.buffer.flip();
		this.updateSource();
	}
	
	public void updateSource(Vector3f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.buffer.flip();
		this.updateSource();
	}
	
	public void updateSource(Vector2f v) {
		BufferTools.putInBuffer(this.buffer, v, 0);
		this.buffer.flip();
		this.updateSource();
	}
	
	public void updateSource(float... values) {
		BufferTools.putInBuffer(this.buffer, 0, values);
		this.buffer.flip();
		this.updateSource();
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

	int getOffset() {
		return this.offset;
	}
	
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
