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
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import core.utils.math.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class BufferTools {
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * @param buffer - the target buffer.
	 * @param offset - offset in machine units.
	 * @param data - the float buffer to write from. It must be readable to OpenGL (not to you!).
	 */
	public static void updateBuffer(int bufferType, int buffer, int offset, FloatBuffer data) {
		GL15.glBindBuffer(bufferType, buffer);
		GL15.glBufferSubData(bufferType, offset<<2, data);
		GL15.glBindBuffer(bufferType, 0);
	}
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * @param buffer - the target buffer.
	 * @param offset - offset in machine units.
	 * @param data - the array to write from.
	 */
	public static void updateBuffer(int bufferType, int buffer, int offset, float[] data) {
    	GL15.glBindBuffer(bufferType, buffer);
		GL15.glBufferSubData(bufferType, offset<<2, data);
		GL15.glBindBuffer(bufferType, 0);
    }
	
	/**
	 * Update a buffer object that is already in the OpenGL pipeline.
	 * @param buffer - the target buffer.
	 * @param offset - offset in machine units.
	 * @param data - the byte buffer to write from. It must be readable to OpenGL (not to you!).
	 */
	public static void updateBuffer(int bufferType, int buffer, int offset, ByteBuffer data) {
		GL15.glBindBuffer(bufferType, buffer);
		GL15.glBufferSubData(bufferType, offset<<2, data);
		GL15.glBindBuffer(bufferType, 0);
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
    
    public static void putInBuffer(FloatBuffer buf, int offset, Matrix4f mat) {
    	FloatBuffer data = BufferUtils.createFloatBuffer(Matrix4f.SIZE);
    	mat.put(data);
    	putInBuffer(buf,offset,data);
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
        buffer.flip();
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
        buffer.flip();
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
    		dest.put(b);
    	}
    	dest.flip();
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
