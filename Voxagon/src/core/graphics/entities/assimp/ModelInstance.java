package core.graphics.entities.assimp;

import core.utils.datatypes.buffers.FloatBufferPartition;

public class ModelInstance {
	private FloatBufferPartition instanceBuffer;

	public ModelInstance(FloatBufferPartition buffer) {
		this.instanceBuffer = buffer;
	}

}
