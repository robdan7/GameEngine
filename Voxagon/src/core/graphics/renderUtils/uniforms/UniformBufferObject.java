package core.graphics.renderUtils.uniforms;


import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import core.graphics.renderUtils.Shaders;
import core.graphics.shading.uniforms.references.UniformBlockReference;
import core.graphics.shading.uniforms.references.UniformBlockReference.UniformTypeException;
import core.utils.datatypes.GlueList;
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
	
	private int UBO;	// size is in machine units.
	private BufferStatus status;
	

	private static HashMap<String, UniformBufferObject> uniformList;
	private static GlueList<Shaders> pendingShaders;
	
	static {
		uniformList = new HashMap<String,UniformBufferObject>();
		pendingShaders = new GlueList<Shaders>();
	}

	private int drawType;
	private GlueList<UniformBufferSource> sources;
	//public StringBuilder uniformCode;
	
	/* The private buffer is only used for allocating space. Every source should have its own buffer.*/
	private FloatBuffer uniformBuffer;
	
	/**
	 * The uniform index associated with this object.
	 * Not to be confused with {@link #currentBuffer}.
	 */
	private int uniformIndex;
	
	private UniformBlockReference blockReference;
	
	/**
	 * New constructor for uniform buffer objects.
	 * @param name
	 */
	public UniformBufferObject(String name) {
		this.blockReference = UniformBlockReference.requestBlockReference(name);
		
		this.uniformIndex = this.blockReference.getbinding();
		
		this.uniformBuffer = BufferUtils.createFloatBuffer(0);
		this.UBO = glGenBuffers();
		
		this.sources = new GlueList<UniformBufferSource>();
		this.drawType = GL20.GL_STATIC_DRAW;
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
	 * Add a new source to this uniform buffer. the index is used for specifying 
	 * what source goes where in the block. Having the index here helps when the block 
	 * is used in the shaders.
	 * @param source
	 * @param index - The desired index for the source. The index will not be correct unless 
	 * every index up to the new source has been bound to the buffer object.
	 */
	void bindBufferSource(UniformBufferSource source, int index) {
		//source.setOffset(this.size);
		try {
			this.blockReference.requestNewMember(index, source.getType());
		} catch (UniformTypeException e) {
			e.printStackTrace();
		}
		source.setIndex(index);
		int i  = 0;
		int offset = 0;
		
		/**
		 * Find the correct spot in the list and add the new source.
		 */
		for (; i < this.sources.size(); i++) {
			if (this.sources.get(i).getIndex() == index) {
				throw new RuntimeException("The index is already added");
			}
			if (this.sources.get(i).getIndex() > index) {
				/* the current index is larger. The new source should be added here.
				 * Break our of the loop and add the new source.
				 * */
				break;
			}
			offset += this.sources.get(i).getStride();
		}
		this.sources.add(i, source);
		source.setOffset(offset);
		
		
		/*
		 * The buffer has been resized. We need to change the offsets for all sources 
		 * with a higher index than the newly added.
		 */
		offset += source.getStride();
		i++;
		UniformBufferSource tempS;
		for (; i < this.sources.size(); i++) {
			tempS = this.sources.get(i);
			tempS.setOffset(offset);
			offset += tempS.getOffset();
		}
		
		
		this.uniformBuffer = BufferTools.combineBuffers(this.uniformBuffer, BufferUtils.createFloatBuffer(source.getStride()));
		this.uniformBuffer.flip();
		this.genBuffer(this.uniformBuffer);
	}
	
	/**
	 * Update the entire uniform buffer object and all the current connected 
	 * sources.
	 */
	void updateSources() {
		this.uniformBuffer.clear();
		for (UniformBufferSource source : this.sources) {
			this.uniformBuffer.put(source.getBuffer());
		}
		BufferTools.updateVertexBuffer(GL_UNIFORM_BUFFER, this.getUBO(), 0, this.uniformBuffer);
	}
	
	/**
	 * Update an individual source in this buffer.
	 * The buffer must be properly flipped or cleared before invoking this method.
	 * @param source - The source to update.
	 */
	void updateSource(UniformBufferSource source) {
		/*if (this.getStatus() != BufferStatus.FINISHED) {
			throw new UniformReadException("The uniform is not finalized!");
		}*/
		//if (source.getOffset() < this.getSize()) {
			BufferTools.updateVertexBuffer(GL_UNIFORM_BUFFER, this.getUBO(), source.getOffset(), source.getBuffer());
		/*} else {
			throw new IndexOutOfBoundsException("Source offset is larger than buffer.");
		}*/
	}

	
	int getUBO() {
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
		}
		return uniformList.get(name);
	}
	
	/**
	 * @return null.
	 */
	@Deprecated
	public StringBuilder getUniformCode() {
		//return this.uniformCode;
		return null;
	}
	
	public int getIndex() {
		return this.uniformIndex;
	}
	
	private int getDrawType() {
		return this.drawType;
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
	@Deprecated
	public static enum glVariableType2 {
		VEC2("vec2",2), VEC3("vec3",4), VEC4("vec4",4), MATRIX4F("mat4",16);
		private String type;
		private int stride;
		
		glVariableType2(String s, int stride) {
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
	
	/**
	 * Keeps track on the uniform status
	 * @author Robin
	 *
	 */
	private static enum BufferStatus {
		INITIATED, FINISHED, PREPARED
	}
	
	public static class UniformReadException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public UniformReadException() {
			super();
		}
		
		public UniformReadException(String arg)  {
			super(arg);
		}
		
	}
}
