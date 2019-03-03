package core.graphics.renderUtils.uniforms.old;

import java.nio.FloatBuffer;

/**
 * This class represents one part of a uniform block.
 * A uniform source stores the actual data that can be put in a uniform block.
 * @author Robin
 *
 */
public class UniformSource {
	
	/**
	 * The index is the given index (1, 2, 3...) for this specific uniform source.
	 */
	private int index = -1;
	
	/**
	 * The float size of this object.
	 */
	private int size;
	
	/**
	 * The connected uniform block.
	 */
	private UniformObject uniformObject;
	
	public UniformSource(int size) {
		//this.buffer = BufferUtils.createFloatBuffer(size);
		this.size = size;
	}
	
	
	/**
	 * Connected this uniform source to a uniform block.
	 * @param o
	 */
	public void bindToUniformObject(UniformObject o) {
		o.bindUniformSource(this);
		this.uniformObject = o;
	}
	
	/**
	 * Upload a float buffer to the connected uniform block.
	 * @param buffer
	 */
	public void updateUniform(FloatBuffer buffer) {
		if (this.uniformObject == null) {
			throw new RuntimeException("This uniform source is not connected to a uniform block.");
		}
		else if (buffer.capacity() != this.size) {
			throw new IllegalArgumentException("Data size is not same as buffer. Buffer: " + buffer.capacity() + " this: " + this.size);
		}
		this.uniformObject.updateUniform(buffer, this);		
	}
	
	/**
	 * Upload a float buffer to the connected uniform block.
	 * @param buffer
	 */
	public void updateUniform(float[] buffer) {
		if (this.uniformObject == null) {
			throw new RuntimeException("This uniform source is not connected to a uniform block.");
		}
		else if (buffer.length != this.size) {
			throw new IllegalArgumentException("Data size is not same as buffer");
		}
		this.uniformObject.updateUniform(buffer, this);		
	}
	
	/**
	 * Get the float size of this uniform source.
	 * @return
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Get the index of this uniform source (1, 2, 3...).
	 * @return
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Set the index of this uniform source.
	 * @param index
	 */
	void setIndex(int index) {
		this.index = index;
	}
}
