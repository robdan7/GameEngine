package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import core.utils.other.BufferTools;

public class UniformSource {
	//private final FloatBuffer buffer;
	private int index = -1;
	private int size;
	private UniformObject uniformObject;
	
	public UniformSource(int size) {
		//this.buffer = BufferUtils.createFloatBuffer(size);
		this.size = size;
	}
	
	public void bindToUniformObject(UniformObject o) {
		o.bindUniformSource(this);
		this.uniformObject = o;
	}
	
	/*public void updateBuffer(float[] data) {
		if (data.length != this.size) {
			throw new IllegalArgumentException("Data size is not same as buffer");
		}
		this.buffer.clear();
		this.buffer.put(data);
		this.buffer.flip();
	}*/
	
	/*public void updateBuffer(FloatBuffer data) {
		if (data.capacity() != this.size) {
			throw new IllegalArgumentException("Data size is not same as buffer");
		}
		this.buffer.clear();
		this.buffer.put(data);
		this.buffer.flip();
	}*/
	
	/**
	 * Upload a float buffer to this uniform. The previous buffer will be overwritten.
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
	
	public void updateUniform(float[] buffer) {
		if (this.uniformObject == null) {
			throw new RuntimeException("This uniform source is not connected to a uniform block.");
		}
		else if (buffer.length != this.size) {
			throw new IllegalArgumentException("Data size is not same as buffer");
		}
		this.uniformObject.updateUniform(buffer, this);		
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	/*
	void BindUniformObject(UniformObject block) {
		this.uniformObject = block;
	}*/

}
