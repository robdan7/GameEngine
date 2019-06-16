package core.graphics.entities.assimp.assets;

import core.utils.datatypes.buffers.FloatBufferPartition;

public class ModelInstance {
	private FloatBufferPartition instanceBuffer;

	public ModelInstance(FloatBufferPartition buffer) {
		this.instanceBuffer = buffer;
	}

}
