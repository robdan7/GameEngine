package core.utils.datatypes.buffers;

import java.nio.FloatBuffer;

/**
 * The float buffer partition is a buffer partition used for storing floats. This is an extension 
 * of the class {@link BufferPartition}.
 * @author Robin
 *
 */
public class FloatBufferPartition extends BufferPartition<FloatBuffer> {

	public FloatBufferPartition(FloatBuffer buffer, int start, int stop) {
		super(buffer ,start, stop);
		
		
	}
	
	/**
	 * Get the next element of this partition.
	 * @return
	 */
	public float get() {
		super.setActivePartition();
		super.position(super.position() + 1);
		return super.getBuffer().get();
	}
	
	/**
	 * Get an element at the specified index of this buffer.
	 * @param index
	 * @return
	 */
	public float get(int index) {
		super.position(index);
		super.setActivePartition();
		return super.getBuffer().get();
	}
	
	public void put(float f) {
		super.setActivePartition();
		super.positionPP();
		super.getBuffer().put(f);
	}
	
	public void put(float[] f) {
		super.setActivePartition();
		super.position(super.position()+f.length);
		super.getBuffer().put(f);

	}
	


}
