package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import core.graphics.renderUtils.uniforms.UniformBufferObject.glVariableType;
import core.utils.math.*;
import core.utils.other.BufferTools;

public class UniformBufferMultiSource implements Iterable<String> {
	private int offset; // offset in bytes.
	private final String[] names;
	private glVariableType type;
	private UniformBufferObject object;
	private FloatBuffer buffer;

	public UniformBufferMultiSource(glVariableType type, String... names) {
		this.names = names;
		this.type = type;
	}
	
	public void updateSource(Vector4f v, int offset) {
		if (offset >= this.names.length) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector3f v, int offset) {
		if (offset >= this.names.length) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v.toVec4f());
		this.object.updateSource(this);
	}
	
	public void updateSource(Vector2f v, int offset) {
		if (offset >= this.names.length) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
	}
	
	public void updateSource(int offset, Vector4f... v) {
		if (v.length > this.names.length) {
			throw new IndexOutOfBoundsException();
		}
		BufferTools.putInBuffer(this.buffer, offset, v);
		this.object.updateSource(this);
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
	
	int getStride() {
		return this.getType().getStride()*this.names.length;
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
