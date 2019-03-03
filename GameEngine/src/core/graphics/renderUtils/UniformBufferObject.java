package core.graphics.renderUtils;

import static core.utils.other.BufferTools.updateBuffer;

import java.util.ArrayList;
import java.util.HashMap;

import core.graphics.renderUtils.Shaders.ShaderCompileException;

/**
 * This class represents a container for uniform block. 
 * All uniform sources must be linked before any shaders can
 * access the uniform block. If no sources are linked, no variables 
 * will be created thus making the uniform object empty.
 * @author Robin
 *
 */
public class UniformBufferObject {
	
	private int size, index, UBO;
	private String name;
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
	private int bindings;
	private String uniformName;
	public StringBuilder uniformCode;
	
	/**
	 * The uniform index associated with this object.
	 * Not to be confused with {@link #currentBuffer}.
	 */
	private final int uniformIndex;

	/**
	 * Create a new uniform buffer object.
	 * @param name - The name GLSL will use to recognize the uniform.
	 */
	public UniformBufferObject(String name) {
		this.uniformIndex = currentUniformIndex++;
		this.uniformCode = new StringBuilder();
		
		// Create a header with the proper index.
		String line = uniformPresets.HEADER.toString().replaceFirst("#index", Integer.toString(this.uniformIndex)).replace("#name", name);
		this.uniformCode.append(line).append("\n");
		
		this.status = BufferStatus.PREPARED;	// The uniform creation has begun. But it is still empty.
		this.name = name;
	}
	
	/**
	 * Upload this uniform block to all pending shaders, and lock it so no more sources can be added.
	 * @throws ShaderCompileException 
	 */
	public void finalize() throws ShaderCompileException {
		this.uniformCode.append(uniformPresets.END.toString()).append("\n");
		
		uniformList.put(this.name, this);
		this.status = BufferStatus.FINISHED;
		for (Shaders s : pendingShaders) {
			s.linkWithUniforms(this.name);
		}
	}
	
	/**
	 * Bind a new source to this uniform object. This method sets the offset of the source
	 * and adds an entry in the generated uniform block code.
	 * @param source - The source to add.
	 * @throws UnsupportedOperationException
	 */
	public void bindBufferSource(UniformBufferSource source) throws UnsupportedOperationException {
		
		if (this.status == BufferStatus.FINISHED) {
			throw new UnsupportedOperationException("The uniform block has already been loaded into GLSL");
		} else {
			source.setOffset(this.size);
			
			// Generate the line containing the new uniform.
			String properties = "layout(offset = " + Integer.toString(this.size) + ") uniform " + source.getType().toString() + " " + source.getName() + ";\n";
			this.uniformCode.append(properties);
			
			// Compute the new size in bytes.
			this.size += source.getType().getStride()<<2;
		}
		
	}
	
	/**
	 * Update an individual source in this object.
	 */
	void updateSource(UniformBufferSource source) {
		updateBuffer(this.getUBO(), source.getOffset(), source.getBuffer());
	}
	
	/**
	 * Update several uniform sources at once.
	 * @param sources
	 */
	void updateSources(UniformBufferSource... sources) {
		
	}
	
	private int getUBO() {
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
		if (!uniformList.containsKey(name)) {
			if (!pendingShaders.contains(shader)) {
				pendingShaders.add(shader);
			}
			return null;
			//throw new IllegalArgumentException("uniform " + name + " does not exist");
		}
		return uniformList.get(name);
	}
	
	/**
	 * Specifies parameters for different OpenGL variable types used in 
	 * uniform blocks.
	 * @author Robin
	 *
	 */
	public static enum glVariableType {
		VEC2("vec2",2), VEC3("vec3",4), VEC4("vec4",4), MATRIX4F("matrix4f",16);
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
