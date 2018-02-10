package utils.rendering;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL15.*;

import utils.fileSystem.OBJLoader;
import utils.math.Vector4f;
import utils.other.BufferTools;

public class Uniform {
	static ArrayList<Integer> usedUniforms = new ArrayList<>();

	public static void createMatrixUniform(Shaders shader, String name) {
		
	}
	
	public static void createUniformBlock(int UBO, FloatBuffer bufferData, int index, int drawType) {
		for (int i : usedUniforms) {
			if (i == index) {
				System.err.println("Uniform index " + index + " is already taken.");
				return;
			}
		}
		glBindBuffer(GL_ARRAY_BUFFER, UBO);
        glBufferData(GL_ARRAY_BUFFER, bufferData, drawType);
        glBindBufferBase(GL_UNIFORM_BUFFER, index, UBO);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        usedUniforms.add(index);
	}
	

	
	public static void updateUniformBlock(int UBO, FloatBuffer bufferData) {
		OBJLoader.updateVBO(UBO, 0, bufferData);
	}

}
