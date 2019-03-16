package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.lwjgl.BufferUtils;

import core.graphics.renderUtils.uniforms.UniformBufferObject.glVariableType;
import core.utils.math.*;
import core.utils.other.BufferTools;

public class UniformBufferSource implements Iterable<String> {
	private int offset; // offset in bytes.
	private final String[] names;
	private glVariableType type;
	private UniformBufferObject object;
	private FloatBuffer buffer;

	public UniformBufferSource(glVariableType type, String... names) {
		this.names = names;
		this.type = type;
		this.buffer = BufferUtils.createFloatBuffer(type.getStride()*names.length);
	}
	
	public UniformBufferSource(UniformBufferObject obj, glVariableType type, String... names) {
		this(type, names);
		this.bindToBufferObject(obj);
	}
	
	public void updateSource(Vector4f v, int offset) {		
		if (offset+glVariableType.VEC4.getStride() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector4f v) {
		this.updateSource(v, 0);
	}
	
	public void updateSource(Vector3f v, int offset) {		
		if (offset+glVariableType.VEC3.getStride() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v.toVec4f());
		this.object.updateSource(this);
	}
	
	public void updateCource(Vector3f v) {
		this.updateSource(v, 0);
	}
	
	public void updateSource(Vector2f v, int offset) {	
		if (offset+glVariableType.VEC2.getStride() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector2f v) {
		this.updateSource(v, 0);
	}
	
	public void updateSource(Matrix4f mat, int offset) {		
		if (offset+glVariableType.MATRIX4F.getStride() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		
		BufferTools.putInBuffer(this.buffer, offset, mat);
		this.object.updateSource(this);
	}
	
	public void updateSource(Matrix4f mat) {
		this.updateSource(mat, 0);
	}
	
	public void updateSource(FloatBuffer buf, int offset) {
		if (offset+buf.capacity() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, buf);
		this.object.updateSource(this);
	}
	 
	public void updateSource(FloatBuffer buf) {
		this.updateSource(buf, 0);
	}
	
	public void updateSource(int offset, Vector4f... v) {		
		if (v.length*glVariableType.VEC4.getStride() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
	}
	
	public void updateSource(int offset, float... data) {
		if (data.length > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, data);
	}
	
	public void updateSource(int offset, Matrix4f... matrices) {
		BufferTools.putInBuffer(this.buffer, offset, matrices);
	}
	
	public void bindToBufferObject(UniformBufferObject o) {
		this.object = o;
		o.bindBufferSource(this);
	}
	
	FloatBuffer getBuffer() {
		return this.buffer;
	}
	
	int  getOffset() {
		return this.offset;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	String[] getNames() {
		return this.names;
	}
	
	glVariableType getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return The length of this source measured in machine units, not index.
	 * This is equal to the private buffer size.
	 */
	public int getStride() {
		return this.buffer.capacity();
	}

	@Override
	public Iterator<String> iterator() {
		
		return new Iterator<String> () {
			int index = 0;
			@Override
			public boolean hasNext() {
				return index < names.length;
			}

			@Override
			public String next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException();
				}
				String result = names[index];
				index++;
				return result;
			}
			
		};
	}


}
