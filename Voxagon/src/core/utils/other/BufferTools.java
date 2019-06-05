/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package core.utils.other;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import core.graphics.renderUtils.VertexAttribute;
import core.utils.math.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferTools {
	
	/**
	 * Create an OpenGL vertex buffer object. The target used here is GL_ARRAY_BUFFER, 
	 * use {@link #createIBO(FloatBuffer, int)} to create an index buffer.
	 * @param data - the buffer to read from. The VBO will be an exact copy of this buffer.
	 * @param usage - GL_STATIC_DRAW if you are not sure what to put here.
	 * @return The generated buffer index. Keep this!
	 */
	public static int createVBO(FloatBuffer data, int usage) {
		int VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return VBO;
	}
	
	/**
	 * Create an OpenGL index buffer object. The target used here is GL_ELEMENT_ARRAY_BUFFER,
	 * use {@link #createVBO(FloatBuffer, int)} to create a vertex buffer.
	 * @param data - The buffer to read from. The IBO will be an exact copy of this buffer.
	 * @param usage - GL_STATIC_DRAW if you are not sure what to put here.
	 * @return
	 */
	public static int createIBO(IntBuffer data, int usage) {
		int glBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glBuffer);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, usage);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		return glBuffer;
	}	
	
	/**
	 * Create an OpenGL vertex array object. The vertex array holds pointers between 
	 * attributes and the actual buffer that stores the vertex data. This enables models to 
	 * be rendered without specifying pointers before every draw call. Remember that the array 
	 * itself does not store the actual data, only pointers to the currently bound buffer.
	 * @param VBO
	 * @param attributes
	 */
	public static int createVAO( int VBO, VertexAttribute... attributes) {
		int VAO = GL30.glGenVertexArrays();
		addVAOattributes(VAO, VBO, attributes);
		return VAO;
	}
	
	/**
	 * Add additional vertex attributes to an already created VAO. These attributes could hold 
	 * data used when rendering instances, or additional data in a separate buffer.
	 * @param VAO
	 * @param VBO
	 * @param attributes
	 */
	public static void addVAOattributes(int VAO, int VBO, VertexAttribute... attributes) {
		GL30.glBindVertexArray(VAO);
		GL15.glBindBuffer(GL30.GL_ARRAY_BUFFER, VBO);
		
		for (VertexAttribute attrib : attributes ) {
			GL30.glEnableVertexAttribArray(attrib.getIndex());
			GL33.glVertexAttribDivisor(attrib.getIndex(), attrib.getDivisor());
			attrib.bindAttribute();
		}
		GL15.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * Store a new float buffer in the same vertex buffer object.
	 * @param target - GL_ARRAY_BUFFER if you are not sure what to put here.
	 * @param buffer - The vertex buffer object.
	 * @param data - the actual float buffer to store.
	 * @param usage - GL_STATIC_DRAW if you are not sure what to put here.
	 */
	public static void revalidateVertexBuffer(int target, int buffer, FloatBuffer data, int usage) {
		GL15.glBindBuffer(target, buffer);
		GL15.glBufferData(target, data, usage);
		GL15.glBindBuffer(target, 0);
	}
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * The buffer is not fliped or cleared before updating.
	 * @param buffer - the OpenGL target type.
	 * @param offset - offset in machine units, not bytes.
	 * @param data - the float buffer to write from. Fliping it is not required.
	 */
	public static void updateVertexBuffer(int target, int buffer, int offset, FloatBuffer data) {
		GL15.glBindBuffer(target, buffer);
		GL15.glBufferSubData(target, offset<<2, data);
		GL15.glBindBuffer(target, 0);
	}
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * @param buffer - the OpenGL target type.
	 * @param offset - offset in machine units, not bytes.
	 * @param data - the array to write from.
	 */
	public static void updateVertexBuffer(int target, int buffer, int offset, float[] data) {
    	GL15.glBindBuffer(target, buffer);
		GL15.glBufferSubData(target, offset<<2, data);
		GL15.glBindBuffer(target, 0);
    }
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * @param buffer - the OpenGL target type.
	 * @param offset - offset in machine units, not bytes.
	 * @param data - the byte buffer to write from. Flipping it is not required.
	 */
	public static void updateVertexBuffer(int target, int buffer, int offset, ByteBuffer data) {
		data.flip();
		GL15.glBindBuffer(target, buffer);
		GL15.glBufferSubData(target, offset<<2, data);
		GL15.glBindBuffer(target, 0);
	}

    /**
     * @param elements the amount of elements to check
     *
     * @return true if the contents of the two buffers are the same, false if not
     */
    public static boolean bufferEquals(FloatBuffer bufferOne, FloatBuffer bufferTwo, int elements) {
        for (int i = 0; i < elements; i++) {
            if (bufferOne.get(i) != bufferTwo.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param matrix4f the Matrix4f that is to be turned into a readable FloatBuffer
     *
     * @return a FloatBuffer representation of matrix4f
     */
    public static FloatBuffer asFloatBuffer(Matrix4f matrix4f) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(Matrix4f.SIZE);
        matrix4f.put(buffer);
        return buffer;
    }
    
    /**
     * @param values the float values that are to be turned into a readable FloatBuffer
     *
     * @return a readable FloatBuffer containing values
     */
    public static FloatBuffer asFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        return buffer;
    }
    
    public static FloatBuffer asFloatBuffer(Vector4f v) {
        //return BufferTools.asFloatBuffer(values.asFloats());
        FloatBuffer buffer = BufferUtils.createFloatBuffer(Vector4f.SIZE);
        buffer.put(v.getX());
        buffer.put(v.getY());
        buffer.put(v.getZ());
        buffer.put(v.getW());
        return buffer;
    }
    
    public static FloatBuffer asFloatBuffer(Vector3f v) {
        //return BufferTools.asFloatBuffer(values.asFloats());
        FloatBuffer buffer = BufferUtils.createFloatBuffer(Vector3f.SIZE);
        buffer.put(v.getX());
        buffer.put(v.getY());
        buffer.put(v.getZ());
        return buffer;
    }
    
    public static FloatBuffer asFloatBuffer(Vector2f v) {
        //return BufferTools.asFloatBuffer(values.asFloats());
        FloatBuffer buffer = BufferUtils.createFloatBuffer(Vector2f.SIZE);
        buffer.put(v.getX());
        buffer.put(v.getY());
        return buffer;
    }
    
    public static void putInBuffer(FloatBuffer buf, Vector4f v, int offset) {
    	buf.clear();
    	buf.position(offset);
    	buf.put(v.getX());
    	buf.put(v.getY());
    	buf.put(v.getZ());
    	buf.put(v.getW());
    }
    
    public static void putInBuffer(FloatBuffer buf, Vector3f v, int offset) {
    	buf.clear();
    	buf.position(offset);
    	buf.put(v.getX());
    	buf.put(v.getY());
    	buf.put(v.getZ());
    }
    
    public static void putInBuffer(FloatBuffer buf, Vector2f v, int offset) {
    	buf.clear();
    	buf.position(offset);
    	buf.put(v.getX());
    	buf.put(v.getY());
    }
    
    public static void putInBuffer(FloatBuffer buf, int offset, float...values) {
    	buf.clear();
    	buf.position(offset);
    	buf.put(values);
    }
    
    public static void putInBuffer(FloatBuffer buf, int offset, Vector4f...values) {
    	buf.clear();
    	float[] temp = new float[values.length<<2];
    	int i = 0;
    	for (Vector4f v : values) {
    		temp[i] = v.getX();
    		temp[i+1] = v.getY();
    		temp[i+2] = v.getZ();
    		temp[i+3] = v.getW();
    		i += 4;
    	}
    	
    	putInBuffer(buf, offset, temp);
    }
    
    public static void putInBuffer(FloatBuffer buf, int offset, Vector3f...values) {
    	buf.clear();
    	float[] temp = new float[values.length<<2];
    	int i = 0;
    	for (Vector3f v : values) {
    		temp[i] = v.getX();
    		temp[i+1] = v.getY();
    		temp[i+2] = v.getZ();

    		i += 3;
    	}
    	
    	putInBuffer(buf, offset, temp);
    }

    public static void putInBuffer(FloatBuffer buf, int offset, Vector2f...values) {
    	buf.clear();
    	float[] temp = new float[values.length<<2];
    	int i = 0;
    	for (Vector2f v : values) {
    		temp[i] = v.getX();
    		temp[i+1] = v.getY();

    		i += 2;
    	}
    	
    	putInBuffer(buf, offset, temp);
    }
    
    public static void putInBuffer(FloatBuffer dest, int offset, Matrix4f... matrices) {
    	//FloatBuffer data = BufferUtils.createFloatBuffer(matrices.length*Matrix4f.SIZE);
    	dest.clear();
    	dest.position(offset);
    	for (Matrix4f m : matrices) {
    		m.put(dest);
    	}
    	
    	//putInBuffer(buf,offset, data);
    }
    
    public static void putInBuffer(FloatBuffer dest, int offset, Matrix4f mat) {
    	/*
    	FloatBuffer data = BufferUtils.createFloatBuffer(Matrix4f.SIZE);
    	mat.put(data);
    	putInBuffer(buf,offset,data);
    	*/
    	dest.clear();
    	dest.position(offset);
    	mat.put(dest);
    }
    
    public static void putInBuffer(FloatBuffer dest, int offset, FloatBuffer data) {
    	dest.clear();
    	dest.position(offset);
    	data.clear();
    	dest.put(data);
    }
    
    /**
     * @param values the float values that are to be turned into a FloatBuffer
     *
     * @return a FloatBuffer readable to OpenGL (not to you!) containing values
     */
    public static FloatBuffer asFlippedFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        return buffer;
    }
    
    /**
     * @param matrix4f the Matrix4f that is to be turned into a FloatBuffer that is readable to OpenGL (but not to you)
     *
     * @return a FloatBuffer representation of matrix4f
     */
    public static FloatBuffer asFlippedFloatBuffer(Matrix4f matrix4f) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix4f.put(buffer);
        return buffer;
    }


    /**
     * @param values the byte values that are to be turned into a readable ByteBuffer
     *
     * @return a readable ByteBuffer
     */
    public static ByteBuffer asByteBuffer(byte... values) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(values.length);
        buffer.put(values);
        return buffer;
    }

    /**
     * @param buffer a readable buffer
     * @param elements the amount of elements in the buffer
     *
     * @return a string representation of the elements in the buffer
     */
    public static String bufferToString(FloatBuffer buffer, int elements) {
        StringBuilder bufferString = new StringBuilder();
        for (int i = 0; i < elements; i++) {
            bufferString.append(" ").append(buffer.get(i));
        }
        return bufferString.toString();
    }
    
    public static FloatBuffer combineBuffers(FloatBuffer... buffers) {
    	int size = 0;
    	for (FloatBuffer b: buffers) {
    		size += b.capacity();
    	}
    	FloatBuffer dest = BufferUtils.createFloatBuffer(size);
    	for (FloatBuffer b: buffers) {
    		b.clear();
    		dest.put(b);
    	}
    	return dest;
    }

    /**
     * @param amountOfElements the amount of elements in the FloatBuffers
     *
     * @return an empty FloatBuffer with a set amount of elements
     */
    public static FloatBuffer reserveData(int amountOfElements) {
        return BufferUtils.createFloatBuffer(amountOfElements);
    }


}
