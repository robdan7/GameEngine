package core.graphics.renderUtils;

import org.lwjgl.opengl.GL20;

import core.graphics.renderUtils.uniforms.old.UniformObject;
import core.utils.fileSystem.FileManager;
import core.utils.other.StringUtils;

import static core.utils.other.StringUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shaders{
	private int shaderProgram;
	private static HashMap<String, StringBuilder> imports = new HashMap<String,StringBuilder>();
	private String vertexFile, fragmentFile;
	private ShaderStatus compileStatus;
	private ArrayList<String> pendingUniforms;
    
	/**
	 * 
	 * @param vert - Vertex shader file name.
	 * @param frag - Fragment shader file name.
	 */
    public Shaders(String vert, String frag) {
    	this.init(vert, frag);
    }
    
    public Shaders(String shaderFile) throws IOException, ShaderCompileException {
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
    		this.pendingUniforms = new ArrayList<String>();
    		this.init(vShader, fShader);
		} catch (IOException e) {
			this.compileStatus = ShaderStatus.FAILED;
			throw new ShaderCompileException("Shader wasn't able to be created");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
    }
    
    /**
     * Initialize the shader program.
     * @param vert
     * @param frag
     */
    private void init(String vert, String frag) {
    	
    	int vertex = 0;
    	int fragment = 0;
    	try {
    		vertex = createShader(vert, GL_VERTEX_SHADER);
        	fragment = createShader(frag, GL_FRAGMENT_SHADER);
    	} catch (ShaderUniformException e) {
    		this.compileStatus = ShaderStatus.PENDING;
    		vertex = 0;
    		fragment = 0;
    		return;
    	}
    	shaderProgram = glCreateProgram();
    	glAttachShader(shaderProgram, vertex);
    	glAttachShader(shaderProgram, fragment);
    	linkProgram();
    	glDeleteShader(vertex);
        glDeleteShader(fragment);
        this.compileStatus = ShaderStatus.FINISHED;
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
     * @throws ShaderUniformException
     */
    private int createShader(String file, int shaderType) throws ShaderUniformException{

        StringBuilder shaderSource = new StringBuilder();
        BufferedReader reader = null;

        
        try {
            reader = FileManager.getReader(file);
            String line="",command = "";
            while ((line = reader.readLine()) != null) {
            	command = line.replaceFirst("\\s*", "");
            	
            	if (command.startsWith(commands.IMPORT.getString())) {
            		String uniform = readBetween(command,commands.UNIFORM.getString(),commands.END.getString());
            		
            		/*
            		UniformBufferObject buffer = UniformBufferObject.requestUniform(this, uniform);
            		if (buffer == null) {
            			if (!this.pendingUniforms.contains(uniform)) {
            				this.pendingUniforms.add(uniform);
            			}
            			throw new ShaderUniformException("uniform object (" + uniform + ") is not finalized!");
            		}
            		*/
            		shaderSource.append(UniformObject.requestUniform(uniform).getUniformCode()).append("\n");
            	} else if (command.startsWith(commands.INCLUDE.getString())) {
            		shaderSource.append(Shaders.getImport(StringUtils.readBetween(command, commands.INCLUDE.getString(), commands.END.getString())));
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
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader wasn't able to be compiled correctly: " + file);
            System.out.println(shaderSource);
        }
        return shader;
	}
    
    /**
     * Load code that can be used in any shader with the "#include" keyword.
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
    		if ((line = line.replaceFirst("\\s*", "")).startsWith(commands.DEFINE.getString())) {
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
    	if (name == "") {
    		throw new IOException("Empty name");
    	}
    	if (imports.containsKey(name)) {
    		throw new IllegalArgumentException("Import file does already exist.");
    	}
    	imports.put(name, result);
    	reader.close();
    }
    
    /**
     * Try to finish the creation of this program.
     * @param uniformName - The uniform that called this function.
     * @return
     * @throws ShaderCompileException
     */
    public boolean linkWithUniforms(String uniformName) throws ShaderCompileException {  
    	boolean result = false;
    	if (this.compileStatus == ShaderStatus.PENDING && this.pendingUniforms.contains(uniformName)) {
    		this.pendingUniforms.remove(uniformName);
    		result = true;
    	}
    	
    	if (this.pendingUniforms.size() == 0) {
    		this.init(this.vertexFile, this.fragmentFile);
    		
        	if (this.compileStatus == ShaderStatus.FINISHED) {
        		result = true;
        	} else {
        		throw new ShaderCompileException("Shader wasn't able to be compiled with specified uniforms.");
        	}
    	}
    	return result;
    }
    
    private static StringBuilder getImport(String name) {
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
	
	public static class ShaderUniformException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ShaderUniformException() {
			super();
		}
		
		public ShaderUniformException (String errorMessage) {
			super(errorMessage);
		}
		
	}
	
	public static class ShaderCompileException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ShaderCompileException() {
			super();
		}
		
		public ShaderCompileException (String errorMessage) {
			super(errorMessage);
		}
	}
	
	/**
	 * This is used for shaders that require some form of uniform block.
	 * A shader can be created before all uniforms, but it cannot be linked.
	 * @author Robin
	 *
	 */
	public static enum ShaderStatus {
		PENDING, FINISHED, FAILED
	}
	
	/**
	 * include: paste in code from a file.
	 * import: Import an element with custom parameters.
	 * define: Define a parameter for the file.
	 * @author Robin
	 *
	 */
	private static enum commands {
		IMPORT("#uniform"), INCLUDE("#include"), DEFINE("#define"), NAME("name"), UNIFORM("uniform"), EQUALS("="), COMMENT("//"), END(";");
		String command;
		private commands(String s) {
			this.command = s;
		}
		
		public String getString() {
			return this.command;
		}
	}

}