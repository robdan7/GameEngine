package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.lwjgl.BufferUtils;

import core.graphics.shading.GLSLvariableType;
import core.utils.math.*;
import core.utils.other.BufferTools;

public class UniformBufferSource implements Iterable<String> {
	private int offset; // offset in bytes.
	private int index;
	private final String[] names;
	private GLSLvariableType type;
	private UniformBufferObject object;
	private FloatBuffer buffer;

	public UniformBufferSource(int index, GLSLvariableType type, String... names) {
		this.names = names;
		this.type = type;
		this.index = index;
		this.buffer = BufferUtils.createFloatBuffer(type.getSize()*names.length);
	}
	
	public UniformBufferSource(UniformBufferObject obj, int index, GLSLvariableType type, String... names) {
		this(index, type, names);
		this.bindToBufferObject(obj, index);
	}
	
	public void updateSource(Vector4f v, int offset) {		
		if ((offset+GLSLvariableType.VEC4.getSize()) > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.updateSource();
	}
	
	public void updateSource(Vector4f v) {
		this.updateSource();
	}
	
	public void updateSource(Vector3f v, int offset) {		
		if ((offset+GLSLvariableType.VEC4.getSize()) > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v.toVec4f());
		this.updateSource();
	}
	
	public void updateSource(Vector3f v) {
		this.updateSource(v, 0);
	}
	
	public void updateSource(Vector2f v, int offset) {	
		if ((offset+GLSLvariableType.VEC4.getSize()) > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.updateSource();
	}
	
	public void updateSource(Vector2f v) {
		this.updateSource(v, 0);
	}
	
	public void updateSource(Matrix4f mat, int offset) {		
		if ((offset+GLSLvariableType.VEC4.getSize()) > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		
		BufferTools.putInBuffer(this.buffer, offset, mat);
		this.updateSource();
	}
	
	public void updateSource(Matrix4f mat) {
		this.updateSource(mat, 0);
	}
	
	public void updateSource(FloatBuffer buf, int offset) {
		if (offset+buf.capacity() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, buf);
		this.updateSource();
	}
	 
	public void updateSource(FloatBuffer buf) {
		this.updateSource(buf, 0);
	}
	
	public void updateSource(int offset, Vector4f... v) {		
		if (v.length*GLSLvariableType.VEC4.getSize() > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.updateSource();
	}
	
	public void updateSource(int offset, float... data) {
		if (data.length > this.getStride()) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, data);
	}
	
	public void updateSource(int offset, Matrix4f... matrices) {
		BufferTools.putInBuffer(this.buffer, offset, matrices);
		this.updateSource();
	}
	
	private void updateSource() {
		this.getBuffer().flip();
		this.object.updateSource(this);
	}
	
	public void bindToBufferObject(UniformBufferObject o, int index) {
		this.object = o;
		o.bindBufferSource(this, index);
	}
	
	FloatBuffer getBuffer() {
		return this.buffer;
	}
	
	/**
	 * Get the buffer offset in machine units.
	 * @return
	 */
	int  getOffset() {
		return this.offset;
	}
	
	int getIndex() {
		return this.index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	String[] getNames() {
		return this.names;
	}
	
	GLSLvariableType getType() {
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
