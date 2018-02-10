package utils.rendering;

import org.lwjgl.opengl.GL20;

import utils.fileSystem.FileManager;
import utils.fileSystem.FileManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shaders{
	private int shaderProgram;
    
	/**
	 * 
	 * @param vert - Vertex shader file name.
	 * @param frag - Fragment shader file name.
	 * @param linkProgram - Set the program to be linked or not.
	 */
    public Shaders(String vert, String frag, boolean linkProgram) {
    	shaderProgram = glCreateProgram();
    	int vertex = 0;
    	int fragment = 0;
    	try {
    		vertex = createShader(vert, GL_VERTEX_SHADER);
        	fragment = createShader(frag, GL_FRAGMENT_SHADER);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	glAttachShader(shaderProgram, vertex);
    	glAttachShader(shaderProgram, fragment);
    	if (linkProgram) {
    		linkProgram();
    	}
    	glDeleteShader(vertex);
        glDeleteShader(fragment);
    }
    
    public void linkProgram() {
    	glLinkProgram(shaderProgram);
    	glValidateProgram(shaderProgram);
    }
    
    /**
     * 
     * @param file - Shader file.
     * @param shaderType - The GL shader type. GL_VERTEX_SHADER or GL_FRAGMENT_SHADER etc.
     * @return - The shader's number.
     */
    private int createShader(String file, int shaderType) {
        int shader = glCreateShader(shaderType);
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = FileManager.getReader(file);
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
        	System.out.println(e);
            System.err.println("Shader wasn't loaded properly: " + file);
            //Display.destroy();
            //System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader wasn't able to be compiled correctly: " + file);
        }
        return shader;
	}
	
    /**
     * Bind a float uniform.
     * @param shaders - The shader number.
     * @param num - The value to bind.
     * @param uniformName - The uniform name.
     */
	public static void createFloatUniform (int shaders, float num, String uniformName) {
		int location = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniform1f(location, num);
	}
	
	/**
	 * Bind a 2D vector uniform.
	 * @param shaders - The shader number,
	 * @param num1 - X parameter.
	 * @param num2- Y parameter.
	 * @param uniformName - The uniform name.
	 */
	public static void create2fUniform (int shaders, float num1, float num2, String uniformName) {
		int location = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniform2f(location, num1, num2);
	}
	
	
	/**
	 * Bind a float uniform.
	 * @param shaders - The shader number.
	 * @param num - The value to bind.
	 * @param uniformName - The uniform name.
	 */
	public static void createIntUniform (int shaders, int num, String uniformName) {
		int location = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniform1i(location, num);
	}
    
    public void dispose() {
        glDeleteProgram(this.shaderProgram);
    }

	
	public int getShaderProgram() {
		// TODO Auto-generated method stub
		return this.shaderProgram;
	}


}