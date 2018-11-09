package core.graphics.renderUtils;

import org.lwjgl.opengl.GL20;

import core.graphics.models.ModelCompiler.Settings;
import core.graphics.renderUtils.uniforms.UniformObject;
import core.utils.fileSystem.FileManager;
import core.utils.other.StringUtils;

import static core.utils.other.StringUtils.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shaders{
	private int shaderProgram;
	private static HashMap<String, StringBuilder> imports = new HashMap<String,StringBuilder>();
    
	/**
	 * 
	 * @param vert - Vertex shader file name.
	 * @param frag - Fragment shader file name.
	 */
    public Shaders(String vert, String frag) {
    	this.init(vert, frag);
    }
    
    public Shaders(String shaderFile) throws IOException {
    	BufferedReader reader = null;
    	try {
    		reader = FileManager.getReader(shaderFile);
    		String line = "", vShader = "", fShader = "";
    		String[] splittedLine;
    		while ((line = reader.readLine()) != null) {
    			line = line.replaceAll("\\s","");
    			if(line.startsWith("//")) {
    				continue;
    			}
    			splittedLine = line.split("=");
    			if (splittedLine.length != 2) {
    				throw new IOException(splittedLine[0] + " property is empty in " + shaderFile);
    			}
    			
    			if (splittedLine[0].contains(Settings.VERTEX_SHADER.getName())) {
    				vShader = splittedLine[1];
    			} else if (splittedLine[0].contains(Settings.FRAGMENT_SHADER.getName())) {
    				fShader = splittedLine[1];
    			}
    		}
    		this.init(vShader, fShader);
		} catch (IOException e) {
			throw new IOException("Shader wasn't able to be created");
		}
    }
    
    private void init(String vert, String frag) {
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
    	linkProgram();
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
    	//TODO Add uniform block automatically.
        int shader = glCreateShader(shaderType);
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = FileManager.getReader(file);
            String line,command;
            while ((line = reader.readLine()) != null) {
            	command = line.replaceFirst("\\s*", "");
            	if (command.startsWith(commands.DEFINE.getString())) {
            		if (command.contains(commands.USE.getString())) {
            			shaderSource.append(UniformObject.requestUniform(readBetween(command,commands.EQUALS.getString(),commands.END.getString())).getUniformCode()).append("\n");
            		}
            	} else if (command.startsWith(commands.IMPORT.getString())) {
            		shaderSource.append(Shaders.getImport(StringUtils.readBetween(command, commands.IMPORT.getString(), commands.END.getString())));
            	} else {
            		shaderSource.append(line).append("\n");
            	}
                
            }
        } catch (IOException e) {
        	System.out.println(e);
            System.err.println("Shader wasn't loaded properly: " + file);
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
     * Load code that can be used in any shader with the "#import" keyword.
     * @param file
     * @throws IOException
     */
    public static void addImport(String file) throws IOException {
    	if (!file.contains(".shd")) {
    		throw new IllegalArgumentException("File contains the wrond extension.");
    	}
    	BufferedReader reader = FileManager.getReader(file);
    	StringBuilder result = new StringBuilder();
    	String line = "";
    	String name = "";
    	int counter = 0;
    	while((line = reader.readLine()) != null) {
    		if (line.replaceFirst("\\s*", "").startsWith(commands.DEFINE.getString())) {
    			line = line.replaceAll("\\s*", "");
    			if (line.contains(commands.NAME.getString())) {
    				name = StringUtils.readBetween(line, commands.EQUALS.getString(), commands.END.getString());
    			} else {
    				throw new IOException("Unsupported syntax on line " + counter);
    			}
    		} else {
    			result.append(line).append("\n");
    		}
    		counter ++;
    	}
    	if (imports.containsKey(name)) {
    		throw new IllegalArgumentException("Import file does already exist.");
    	}
    	imports.put(name, result);
    	reader.close();
    }
    
    public static StringBuilder getImport(String name) {
    	if (!imports.containsKey(name)) {
    		throw new IllegalArgumentException("Import does not exist.");
    	}
    	return imports.get(name);
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
		return this.shaderProgram;
	}
	
	public static enum Settings {
		VERTEX_SHADER("vShader"), FRAGMENT_SHADER("fShader");
		
		String name = "";
		
		private Settings(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	

	private static enum commands {
		DEFINE("#define"), IMPORT("#import"), NAME("name"), USE("uniform"), EQUALS("="), COMMENT("//"), END(";");
		String command;
		private commands(String s) {
			this.command = s;
		}
		
		public String getString() {
			return this.command;
		}
	}

}