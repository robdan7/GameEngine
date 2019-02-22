package core.graphics.renderUtils.uniforms;

import java.nio.FloatBuffer;

import core.graphics.models.OBJLoader;
import core.graphics.renderUtils.Shaders;
import core.utils.datatypes.Pair;
import core.utils.other.BufferTools;

import static org.lwjgl.opengl.GL30.*;

public class UniformTools {
	//static ArrayList<Integer> usedUniforms = new ArrayList<>();

	public static void createMatrixBlock(Shaders shader, String name) {
		throw new RuntimeException("This function is not supported");
	}
	
	/**
	 * Create a uniform block from an array of floats. Remember to set the offset in the shaders based on
	 * the position of the array data (measured in bytes). Tip: <br>Use layout (std140, binding = n) where n is the index.
	 * @param bufferData - Array with data.
	 * @param index - The shader index to use. All shaders with the index can use the created uniform block.
	 * @param drawType - GL_DYNAMIC_DRAW, GL_STATIC_DRAW or GL_STREAM_DRAW.
	 * @return A pair with the created float buffer and the buffer index used for binding.
	 */
	
	/*public static Pair<FloatBuffer, Integer> createUniformBlock(float[] bufferData, UniformObject index, int drawType) {
		Pair<FloatBuffer, Integer> data = createFloatBuffer(index, bufferData);
		UniformTools.bindBuffer(data.getFirst(), data.getSecond(), index, drawType);
        return data;
	}*/
	
	/**
	 * Create a uniform block from an array of floats. Remember to set the offset in the shaders based on
	 * the position of the array data (measured in bytes). Tip: <br>Use layout (std140, binding = n) where n is the index.
	 * @param bufferData - The float buffer to use.
	 * @param index - The shader index to use. All shaders with the index can use the created uniform block.
	 * @param drawType - GL_DYNAMIC_DRAW, GL_STATIC_DRAW or GL_STREAM_DRAW.
	 * @return The buffer index used for binding.
	 */
	/*public static UniformObject createUniformBlock(FloatBuffer bufferData, int index, int drawType) {
		UniformObject uniform = new UniformObject(index);
		int UBO = glGenBuffers();
		UniformTools.bindBuffer(bufferData, UBO, uniform, drawType);
		return uniform;
	}*/
	
	/*private static void bindBuffer(FloatBuffer data, int UBO, UniformObject index, int drawType) {
		glBindBuffer(GL_ARRAY_BUFFER, UBO);
		glBufferData(GL_ARRAY_BUFFER, data, drawType);
		glBindBufferBase(GL_UNIFORM_BUFFER, index.getIndex(), UBO);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}*/
	
	@Deprecated
	private static Pair<FloatBuffer, Integer> createFloatBuffer(UniformObject index, float[] vertices) {
		FloatBuffer buf = BufferTools.asFloatBuffer(vertices);
		buf.flip();
		int UBO = glGenBuffers();
		return new Pair<FloatBuffer, Integer>(buf, UBO);
	}
	
	static void updateUniformBlock(int UBO, FloatBuffer bufferData, int offset) {
		OBJLoader.updateVBO(UBO, offset, bufferData);
	}

}
