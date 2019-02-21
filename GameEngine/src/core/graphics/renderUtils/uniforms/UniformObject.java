package core.graphics.renderUtils.uniforms;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import core.graphics.models.OBJLoader;
import core.utils.fileSystem.FileManager;
import static core.utils.other.StringUtils.*;

/**
 * This class is used to update uniforms in the render pipeline. Several uniform sources can be connected
 * to the same uniform, thus forming a uniform block. An instance of this object does not keep track of 
 * the byte offsets in the shader for every uniform, but the float number offsets can be retrieved.
 * 
 * The uniform block does not store any uniform data, that's why uniform sources are used.
 * @author Robin
 *
 */
public class UniformObject {
	/**
	 * Every uniform has an index. This variable keeps track of it.
	 */
	private static int currentUniformIndex = 0;
	
	// List of all uniform blocks created.
	private static HashMap<String, UniformObject> uniformList = new HashMap<String,UniformObject>();
	
	// Local uniform data.
	private int UBO;
	private int drawType;
	private int size;
	private int bindings;
	private String uniformName;
	StringBuilder uniformCode;
	
	/**
	 * The uniform index associated with this object.
	 * Not to be confused with {@link #currentBuffer}.
	 */
	private final int uniformIndex;
	
	/**
	 * List of all connected uniform sources.
	 */
	private ArrayList<UniformSource> uniformSources;
	
	// remove this.
	//private int[] buffersizes;
	
	/**
	 * The total offset in the uniform buffer per index.
	 */
	private int[] bufferIndexOffset;
	
	/**
	 * Counter for how many indexes have been used.
	 */
	private int currentBuffer = 0;
	
	/**
	 * Create a uniform with index and draw type.
	 * @param file - The .unf file to read.
	 * @param drawType - GL_STATIC_DRAW, GL_DYNAMIC_DRAW or GL_STREAM_DRAW.
	 */
	public UniformObject(String file, int drawType) {
		this.uniformIndex = currentUniformIndex++;
		this.drawType = drawType;
		this.uniformCode = new StringBuilder();
		try {
			this.readUniform(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Two uniforms can't have the same name.
		if (uniformList.containsKey(this.uniformName)) {
			throw new RuntimeException("The name in " + file + " is already used");
		}
		uniformList.put(this.uniformName, this);
		
		this.bufferIndexOffset = new int[this.bindings];
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(this.size);
				
		this.UBO = glGenBuffers();
				
		this.bindBuffer(buffer);
		
		uniformSources = new ArrayList<UniformSource>();
	}
	
	/**
	 * Connect a uniform source to this uniform object.
	 * The source is given an index, and will be connected to this uniform object.
	 * @param u
	 */
	void bindUniformSource(UniformSource u) {
		// Check if all bindings have been used.
		if (this.currentBuffer == this.bufferIndexOffset.length) {
			throw new RuntimeException("The total buffer index size is reached");
		}
		// Check if the memory is full.
		else if ((currentBuffer + 1 < this.bufferIndexOffset.length) && this.bufferIndexOffset[currentBuffer + 1] >= this.size) {
			throw new RuntimeException("No more space available. Used: " + this.bufferIndexOffset[this.currentBuffer+1] + ", available: " + this.size);
		}
		// Add offset to the next index.
		if (currentBuffer < this.bufferIndexOffset.length-1) {
			this.bufferIndexOffset[currentBuffer + 1] = bufferIndexOffset[currentBuffer] + u.getSize();
		}
		
		// Everything is OK. Add the source to this object.
		u.setIndex(currentBuffer++);
		uniformSources.add(u);
	}	
	
	/**
	 * This method stores data at the specified uniform source index.
	 * @param data - The data that should be stored.
	 * @param u - The uniform source.
	 */
	void updateUniform(FloatBuffer data, UniformSource u) {
		if (!this.uniformSources.contains(u)) {
			throw new RuntimeException("the provided source is not a valid object for this uniforms");
		}
		data.clear();
		OBJLoader.updateVBO(this.getUBO(), this.bufferIndexOffset[u.getIndex()], data);
	}
	
	/**
	 * This method stores data at the specified uniform source index.
	 * @param data - The data that should be stored.
	 * @param u - The uniform source.
	 */
	void updateUniform(float[] data, UniformSource u) {
		if (!this.uniformSources.contains(u)) {
			throw new RuntimeException("the provided source is not a valid object for this uniforms");
		}
		OBJLoader.updateVBO(this.getUBO(), this.bufferIndexOffset[u.getIndex()], data);
		//data.clear();
	}
	
	private void bindBuffer(FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, this.getUBO());
		glBufferData(GL_ARRAY_BUFFER, data, drawType);
		glBindBufferBase(GL_UNIFORM_BUFFER, this.getIndex(), this.getUBO());
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Reads a uniform file (.unf) and puts some values in it.
	 * This method is meant to translate written code into usable variables.
	 * @param f
	 * @throws IOException
	 */
	private void readUniform(String f) throws IOException {
		if (!f.contains(commands.FILENAME.getString())) {
			throw new IOException("Unsupported file excension.");
		}
		
		BufferedReader reader = FileManager.getReader(f);
		String line = "";
		
		while ((line = reader.readLine()) != null) {
			line = removeAfter(line, commands.COMMENT.getString());
			if (line.replaceFirst("\\s*", "").startsWith(commands.DEFINE.getString())) {
				line = line.replaceAll("\\s*", "");
				if (line.contains(commands.NAME.getString())) {
					this.uniformName = readBetween(line, commands.EQUALS.getString(),commands.END.getString());
				} else if (line.contains(commands.BINDINGS.getString())) {
					this.bindings = Integer.parseInt(readBetween(line,commands.EQUALS.getString(), commands.END.getString()));
				} else if (line.contains(commands.SIZE.getString())) {
					this.size = Integer.parseInt(readBetween(line,commands.EQUALS.getString(), commands.END.getString()));
				}
			} else {
				if (line.contains(commands.INDEX.getString())) {
					line = line.replaceFirst(commands.INDEX.getString(), "" + this.uniformIndex);
				}
				this.uniformCode.append(line).append("\n");
			}
			
		}
		
		reader.close();
		
		if (this.size <= 0 || this.uniformName == "" || this.uniformName.contains("\\s*") || this.bindings <= 0) {
			System.out.println("." + this.size + ":" + this.uniformName + ":" + this.bindings);
			throw new RuntimeException("Uniform file is corrupt.");
		}
	}

	
	/**
	 * This represents a list of acceptable commands in a uniform file.
	 *
	 */
	private static enum commands{
		DEFINE("#define"), NAME("name"), INDEX("#index"), SIZE("size"), BINDINGS("bindings"), EQUALS("="), END(";"), FILENAME(".unf"), COMMENT("//");
		String s;
		private commands(String s) {
			this.s = s;
		}
		
		public String getString() {
			return this.s;
		}
	}
	
	/**
	 * Get the total buffer offset for a specified uniform index.
	 * @param index - The uniform source index. Starts with 0,1,2,3...
	 * @return
	 */
	public int getBufferOffset(int index) {
		return this.bufferIndexOffset[index];
	}
	
	/**
	 * Get the buffer index for this uniform.
	 * @return
	 */
	public int getUBO() {
		return this.UBO;
	}
	
	/**
	 * Get the index of this uniform object for any shader.
	 * @return
	 */
	public int getIndex() {
		return this.uniformIndex;
	}
	
	public StringBuilder getUniformCode() {
		return this.uniformCode;
	}
	
	/**
	 * Return a uniform block based on the name provided in the shaders.
	 * @param name
	 * @return
	 */
	public static UniformObject requestUniform(String name) {
		if (!UniformObject.uniformList.containsKey(name)) {
			throw new IllegalArgumentException("uniform does not exist");
		}
		return UniformObject.uniformList.get(name);
	}
}
