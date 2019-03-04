package core.graphics.renderUtils.uniforms;

import static core.utils.other.BufferTools.updateBuffer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import core.graphics.models.OBJLoader;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.utils.other.BufferTools;

/**
 * This class represents a container for uniform block. 
 * All uniform sources must be linked before any shaders can
 * access the uniform block. If no sources are linked, no variables 
 * will be created thus making the uniform object empty.
 * @author Robin
 *
 */
public class UniformBufferObject {
	
	private int size, UBO;	// size is in machine units.
	private BufferStatus status;
	
	/**
	 * Every uniform has an index. This variable keeps track of it.
	 */
	private static int currentUniformIndex = 0;
	

	private static HashMap<String, UniformBufferObject> uniformList;
	private static ArrayList<Shaders> pendingShaders;
	
	static {
		uniformList = new HashMap<String,UniformBufferObject>();
		pendingShaders = new ArrayList<Shaders>();
	}

	private int drawType;
	private String uniformName;
	public StringBuilder uniformCode;
	
	private FloatBuffer uniformBuffer;
	
	/**
	 * The uniform index associated with this object.
	 * Not to be confused with {@link #currentBuffer}.
	 */
	private final int uniformIndex;

	/**
	 * Create a new uniform buffer object.
	 * @param name - The name GLSL will use to recognize the uniform.
	 */
	public UniformBufferObject(String name, int drawType) {
		this.uniformIndex = currentUniformIndex++;
		this.uniformCode = new StringBuilder();
		this.drawType = drawType;
		
		// Create a header with the proper index.
		String line = uniformPresets.HEADER.toString().replaceFirst("#index", Integer.toString(this.uniformIndex)).replace("#name", name);
		this.uniformCode.append(line).append("\n");
		
		this.status = BufferStatus.PREPARED;	// The uniform creation has begun. But it is still empty.
		this.uniformName = name;
		
		// Create a currently empty buffer. It will be resized later.
		this.uniformBuffer = BufferUtils.createFloatBuffer(this.size);
		this.UBO = glGenBuffers();	
	}
	
	/**
	 * Upload this uniform block to all pending shaders, and lock it so no more sources can be added.
	 * This is the last thing that should be done when creating a uniform block.
	 * Nothing will be done if the uniform is already finished.
	 * @throws ShaderCompileException 
	 */
	public void finalizeBuffer() throws ShaderCompileException {
		if (this.getStatus() == BufferStatus.FINISHED) {
			return;
		}
		this.uniformCode.append(uniformPresets.END.toString());
		
		uniformList.put(this.uniformName, this);
		this.status = BufferStatus.FINISHED;
		for (Shaders s : pendingShaders) {
			s.linkWithUniforms(this.uniformName);
		}
		
		this.genBuffer(this.uniformBuffer);
	}
	
	/**
	 * Wipe all previous data and create a new buffer.
	 * @param data
	 */
	private void genBuffer(FloatBuffer data) {
		glBindBuffer(GL_UNIFORM_BUFFER, this.getUBO());
		glBufferData(GL_UNIFORM_BUFFER, data, this.getDrawType());
		glBindBufferBase(GL_UNIFORM_BUFFER, this.getIndex(), this.getUBO());
		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}
	
	/**
	 * Bind a new source to this uniform object. This method sets the offset of the source
	 * and adds an entry in the generated uniform block code.
	 * @param source - The source to add.
	 * @throws UnsupportedOperationException
	 */
	void bindBufferSource(UniformBufferSource source) throws UnsupportedOperationException {
		
		if (this.status == BufferStatus.FINISHED) {
			throw new UnsupportedOperationException("The uniform block has already been loaded into GLSL");
		} else {
			source.setOffset(this.size);	// The offset is equal to the old size.
			
			// Generate the line containing the new uniform. the size is shifted to get the bytes.
			String properties = "layout(offset = " + Integer.toString(this.getSize()<<2) + ") uniform " + source.getType().toString() + " " + source.getName() + ";\n";
			this.uniformCode.append(properties);
			
			// Update the size so the new source can get some space too.
			this.size += source.getStride();
			
			// The buffer size has changed. Expand it and generate the buffer again.
			this.uniformBuffer = BufferTools.combineBuffers(this.uniformBuffer, BufferUtils.createFloatBuffer(source.getStride()));
			this.genBuffer(this.uniformBuffer);
		}
	}
	
	void bindBufferSource (UniformBufferMultiSource source) {
		
		if (this.status == BufferStatus.FINISHED) {
			throw new UnsupportedOperationException("The uniform block has already been loaded into GLSL");
		} else {
			source.setOffset(this.size);	// The offset is equal to the old size.
			
			// Generate the line containing the new uniform. the size is shifted to get the bytes.
			
			for (String name : source) {
				String properties = "layout(offset = " + Integer.toString(this.getSize()<<2) + ") uniform " + source.getType().toString() + " " + name + ";\n";
				this.uniformCode.append(properties);
				// Update the size so the new source can get some space too.
				this.size += source.getType().getStride();
			}
			
			// The buffer size has changed. Expand it and generate the buffer again.
			this.uniformBuffer = BufferTools.combineBuffers(this.uniformBuffer, BufferUtils.createFloatBuffer(source.getStride()));
			this.genBuffer(this.uniformBuffer);
		}
	}
	
	/**
	 * Update an individual source in this buffer.
	 * The buffer in the source does not have to be flipped.
	 */
	void updateSource(UniformBufferSource source) {
		if (this.getStatus() != BufferStatus.FINISHED) {
			try {
				throw new Exception("The uniform is not finished");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (source.getOffset() < this.getSize()) {
			//updateBuffer(this.getUBO(), source.getOffset(), source.getBuffer().clear());
			//OBJLoader.updateVBO(this.getUBO(), source.getOffset(), source.getBuffer().flip());
			glBindBuffer(GL_UNIFORM_BUFFER, this.getUBO());
			GL15.glBufferSubData(GL_UNIFORM_BUFFER, source.getOffset()<<2, source.getBuffer().clear());
			glBindBuffer(GL_UNIFORM_BUFFER, 0);
		} else {
			throw new IndexOutOfBoundsException("Source offset is larger than buffer.");
		}
		
	}
	
	void updateSource(UniformBufferMultiSource source) {
		if (this.getStatus() != BufferStatus.FINISHED) {
			try {
				throw new Exception("The uniform is not finished");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (source.getOffset() < this.getSize()) {
			//updateBuffer(GL_UNIFORM_BUFFER, this.getUBO(), source.getOffset(), source.getBuffer().flip());
			glBindBuffer(GL_UNIFORM_BUFFER, this.getUBO());
			GL15.glBufferSubData(GL_UNIFORM_BUFFER, source.getOffset()<<2, source.getBuffer().clear());
			glBindBuffer(GL_UNIFORM_BUFFER, 0);
			//OBJLoader.updateVBO(this.getUBO(), source.getOffset(), source.getBuffer().clear());
		} else {
			throw new IndexOutOfBoundsException("Source offset is larger than buffer.");
		}
	}
	
	/**
	 * Update several uniform sources at once.
	 * @param sources
	 */
	void updateSources(UniformBufferSource... sources) {
		// TODO fix this, dingus.
	}
	
	public int getUBO() {
		return this.UBO;
	}
	
	/**
	 * Return a uniform block based on the name provided in the shaders.
	 * No uniform will be returned if the uniform has not been completed or does
	 * not exist yet. The shader will then be added in a list, and invoked when the uniform
	 * becomes available.
	 * @param name - The unique uniform name used in GLSL.
	 * @return The uniform or null if it does not exist.
	 */
	public static UniformBufferObject requestUniform(Shaders shader, String name) {
		UniformBufferObject result = uniformList.get(name);
		if (result == null || result.getStatus() != BufferStatus.FINISHED) {
			if (!pendingShaders.contains(shader)) {
				pendingShaders.add(shader);
			}
			return null;
			//throw new IllegalArgumentException("uniform " + name + " does not exist");
		}
		return uniformList.get(name);
	}
	
	public StringBuilder getUniformCode() {
		return this.uniformCode;
	}
	
	public int getIndex() {
		return this.uniformIndex;
	}
	
	private int getDrawType() {
		return this.drawType;
	}
	
	/**
	 * Get the size of this buffer in basic machine units.
	 * @return
	 */
	private int getSize() {
		return this.size;
	}
	
	private BufferStatus getStatus() {
		return this.status;
	}
	
	/**
	 * Specifies parameters for different OpenGL variable types used in 
	 * uniform blocks.
	 * @author Robin
	 *
	 */
	public static enum glVariableType {
		VEC2("vec2",2), VEC3("vec3",4), VEC4("vec4",4), MATRIX4F("mat4",16);
		private String type;
		private int stride;
		
		glVariableType(String s, int stride) {
			this.type = s;
			this.stride = stride;
		}
		
		/**
		 * 
		 * @return The number of machine units this variable type requires.
		 */
		int getStride() {
			return this.stride;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	}
	
	private static enum uniformPresets {
		HEADER("layout (std140, binding = #index) uniform #name {"), END("}");
		private String text;
		
		uniformPresets(String str) {
			this.text = str;
		}
		
		@Override
		public String toString() {
			return this.text;
		}
	}
	
	/**
	 * Keeps track on the uniform status
	 * @author Robin
	 *
	 */
	private static enum BufferStatus {
		INITIATED, FINISHED, PREPARED
	}
}
