package core.graphics.renderUtils.buffers;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import core.graphics.renderUtils.Shaders;
import core.utils.other.Texture;

public class Gbuffer extends Drawbuffer {

	public Gbuffer(int width, int height, int imageFilter, String[] colorNames, int[] colorFormats, int[] baseFormats) throws Exception {
		super.BUFFER_WIDTH = width;
    	super.BUFFER_HEIGHT = height;
    	super.IMAGE_FILTER = imageFilter;
        super.colorbuffer = glGenFramebuffers();
        
        super.textures = new Texture[colorNames.length+1];
        for (int i = 0 ; i < colorNames.length; i++) {
        	super.textures[i] = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, colorNames[i], imageFilter, colorFormats[i], baseFormats[i]);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, colorbuffer);
        
        for (int i = 0 ; i < colorNames.length; i++) {
        	GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, super.textures[i].getId(), 0);
        }
        
        int[] colorAttachements = new int[colorNames.length];
        for (int i = 0; i < colorAttachements.length; i++) {
        	colorAttachements[i] = GL_COLOR_ATTACHMENT0 + i;
        }
        GL20.glDrawBuffers(colorAttachements);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Bind a depth texture to this buffer.
	 * @param name - Shader uniform name.
	 * @param format - Depth format.
	 */
	public void attachDepthTexture(String name, int format) {
		super.textures[super.textures.length-1] = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, name, super.IMAGE_FILTER, format, GL_DEPTH_COMPONENT);
		glBindFramebuffer(GL_FRAMEBUFFER, super.colorbuffer);
		GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, super.textures[super.textures.length-1].getId(), 0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	/**
	 * Bind all textures as uniforms to a specified shader.
	 * Remember to bind the shader first.
	 */
	@Override
	public void bindAllTextures(Shaders shader) {
		for (int i = 0; i < super.textures.length-1; i++) {
				super.textures[i].bindAsUniform(shader);
    	}
		if (super.textures[super.textures.length-1] != null) {
			super.textures[super.textures.length-1].bindAsUniform(shader);
		}
	}
}
