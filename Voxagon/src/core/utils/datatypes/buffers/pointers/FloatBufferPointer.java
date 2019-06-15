package core.utils.datatypes.buffers.pointers;

import java.nio.FloatBuffer;

public class FloatBufferPointer implements BufferPointer<FloatBuffer> {
	private FloatBuffer buffer;

	public FloatBufferPointer(FloatBuffer buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public void changeBuffer(FloatBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public FloatBuffer getBuffer() {
		return this.buffer;
	}
	

}
