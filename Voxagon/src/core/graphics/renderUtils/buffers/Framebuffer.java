package core.graphics.renderUtils.buffers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import core.graphics.misc.Texture;
import core.graphics.renderUtils.Shaders;
import core.utils.datatypes.GlueList;
import core.utils.math.Vector4f;

/**
 * This class should represent one (1) frame buffer. A frame buffer contains a 
 * texture and color information used for rendering to the texture. Mimpapping is not enabled. I don't know how
 * to do that, yet.
 * @author Robin
 *
 */
public class Framebuffer {
	private GlueList<Texture> textures;
	private Texture depthAttachment;
	private int framebuffer;
	private int BUFFER_WIDTH, BUFFER_HEIGHT;
	private Vector4f backgroundColor;
	
	private boolean finalized = false;

	public Framebuffer(int width, int height) {
		this.backgroundColor = new Vector4f();
		this.textures = new GlueList<Texture>();
		this.framebuffer = glGenFramebuffers();
		this.BUFFER_WIDTH = width;
		this.BUFFER_HEIGHT =  height;
	}

	
	/**
	 * Add a texture to this frame buffer. The frame buffer textures are the textures OpenGL writes to 
	 * after a completed render. Several textures can be connected to the same frame buffer and have different 
	 * purposes. One can contain the depth value, and another can contain the color.
	 * 
	 * @param name - The GLSL uniform name
	 * @param imageFilter - OpenGL image filter function. e.g. GL_LINEAR
	 * @param internalFormat - The number of color components in the texture.
	 * @param baseFormat - The format of the pixel data.
	 */
	public void addTexture(String name, int imageFilter, int internalFormat, int baseFormat) {
		Texture tex = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, name, imageFilter, internalFormat, baseFormat);
		int currentTexture = GL_COLOR_ATTACHMENT0 + this.textures.size();
		
		glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
		GL32.glFramebufferTexture(GL_FRAMEBUFFER, currentTexture, tex.getId(),0);
	
		//GL20.glDrawBuffers(currentTexture);
		//glReadBuffer(GL_COLOR_ATTACHMENT0);
		if (this.finalized || glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create FrameBuffer texture");
        }
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		this.textures.add(tex);
	}
	
	/**
	 * Set the depth attachment for this frame buffer.
	 * @param name - The GLSL uniform name.
	 * @param imageFilter - Image filter.
	 * @param internalFormat - Internal format of type GL_DEPTH_COMPONENT.
	 */
	public void addDepthAttachment(String name, int imageFilter, int internalFormat) {
		this.depthAttachment = new Texture(this.BUFFER_WIDTH, this.BUFFER_HEIGHT, name, imageFilter, internalFormat, GL_DEPTH_COMPONENT);
		
		glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
		GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthAttachment.getId(), 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public Texture getDepthAttachment() {
		return this.depthAttachment;
	}
	
	public int getHeight() {
		return this.BUFFER_HEIGHT;
	}
	
	public int getWidth() {
		return this.BUFFER_WIDTH;
	}
	
	/**
	 * Set the background color for this buffer. The background will be transparent if 
	 * no color is set.
	 * @param color
	 */
	public void setClearColor(Vector4f color) {
		this.backgroundColor.set(color);
	}
	
	public void setClearColor(float r, float g, float b, float a) {
		this.backgroundColor.set(r,g,b,a);
	}
	
	/**
	 * Clear the buffer. The buffer must be loaded first.
	 */
	public void clearBuffer() {
		glClearColor(this.backgroundColor.getX(), this.backgroundColor.getY(), this.backgroundColor.getZ(), this.backgroundColor.getW()); // Set the background to transparent.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void bindBuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, this.getFramebuffer());
	}
	
	public void completeFramebuffer() {
		if (!this.finalized) {
			int[] attachments = new int[this.textures.size()];
			for (int i = 0; i < attachments.length; i++) {
				attachments[i] = GL_COLOR_ATTACHMENT0+i;
			}
			glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
			GL20.glDrawBuffers(attachments);
			//glReadBuffer(GL_COLOR_ATTACHMENT0);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			this.finalized = true;
		}
	}
	
	public int getFramebuffer() {
		return this.framebuffer;
	}
	
	/**
	 * Bind all frame buffer textures to a shader. The textures should be used in the deffered shading 
	 * stage.
	 * @param shader
	 */
	public void bindTextures(Shaders shader) {
		for (Texture tex : this.textures) {
			tex.bindAsUniform(shader);
		}
		this.depthAttachment.bindAsUniform(shader);
	}
	
	public void cleanup() {
		glDeleteFramebuffers(this.framebuffer);
		for (Texture tex : this.textures) {
			tex.cleanup();
		}
	}
}
