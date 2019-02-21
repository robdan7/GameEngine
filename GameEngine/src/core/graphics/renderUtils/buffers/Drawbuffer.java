package core.graphics.renderUtils.buffers;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import core.graphics.renderUtils.Shaders;
import core.utils.other.Texture;

public class Drawbuffer {

    protected int BUFFER_WIDTH, BUFFER_HEIGHT, IMAGE_FILTER;

    protected int colorbuffer;

    protected Texture[] textures;
    protected Texture depthtexture;
    protected Texture primaryTexture;
    
    protected Drawbuffer() {
    	
    }

    public Drawbuffer(String depthtextureName, int width, int height, int imageFilter, String... colorTextures) throws Exception {
    	BUFFER_WIDTH = width;
    	BUFFER_HEIGHT = height;
    	IMAGE_FILTER = imageFilter;
        colorbuffer = glGenFramebuffers();
        
        textures = new Texture[colorTextures.length];
        for (int i = 0 ; i < textures.length; i++) {
        	textures[i] = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, colorTextures[i], imageFilter, GL_RGBA, GL_RGBA);
        }
       // texture0 = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, colortextureName, imageFilter, GL_RGBA);
        depthtexture = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, depthtextureName, imageFilter, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT);
        primaryTexture = textures[0];
        glBindFramebuffer(GL_FRAMEBUFFER, colorbuffer);
        
        for (int i = 0 ; i < textures.length; i++) {
        	GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, textures[i].getId(), 0);
        }
        //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthtexture.getId(), 0);
        GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthtexture.getId(), 0);
        //glDrawBuffer(GL_COLOR_ATTACHMENT0);
        
        int[] colorAttachements = new int[colorTextures.length];
        for (int i = 0; i < colorAttachements.length; i++) {
        	colorAttachements[i] = GL_COLOR_ATTACHMENT0 + i;
        }
        GL20.glDrawBuffers(colorAttachements);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        //this.texture.bindAsUniform(shader.getShaderProgram());
    }
    
    public void bindAllTextures(Shaders shader) {
    	for (Texture t : this.textures) {
    		t.bindAsUniform(shader);
    	}
    	this.depthtexture.bindAsUniform(shader);
    }
    
    public Drawbuffer(String depthtextureName, int width, int height) throws Exception {
    	BUFFER_WIDTH = width;
    	BUFFER_HEIGHT = height;
        colorbuffer = glGenFramebuffers();
        //depthbuffer = GL30.glGenRenderbuffers();

        //texture = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, colortextureName, GL_LINEAR, GL11.GL_RGBA);
        depthtexture = new Texture(BUFFER_WIDTH, BUFFER_HEIGHT, depthtextureName, GL_NEAREST, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT);
        primaryTexture = depthtexture;
        glBindFramebuffer(GL_FRAMEBUFFER, colorbuffer);
        //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
        GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthtexture.getId(), 0);
        //glEnable(GL_DEPTH_TEST);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }
        
       //GL30.glBindRenderbuffer(GL_RENDERBUFFER,depthbuffer);
        //GL30.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT);
        //GL30.glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        
        //GL30.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbuffer);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        //GL30.glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
    }

    public Texture getPrimaryTexture() {
		return primaryTexture;
    }

    public Texture getColorMapTexture() {
        return textures[0];
    }
    
    public Texture getDepthMapTexture() {
        return depthtexture;
    }
 

    public int getColorMapFBO() {
        return colorbuffer;
    }
    public int getWidth() {
    	return this.BUFFER_WIDTH;
    }
    
    public int getHeight() {
    	return this.BUFFER_HEIGHT;
    }
    
    public void cleanup() {
        glDeleteFramebuffers(colorbuffer);
        if (this.textures[0] != null) {
        	this.textures[0].cleanup();
        }
        textures[0].cleanup();
        if (this.depthtexture != null) {
        	this.depthtexture.cleanup();
        }
    }
    
    public Drawbuffer getThis() {
    	return this;
    }
}