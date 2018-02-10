package utils.rendering;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import utils.other.Texture;

public class Drawbuffer {

    public int SHADOW_MAP_WIDTH;

    public int SHADOW_MAP_HEIGHT;

    private int colorbuffer;
    private int depthbuffer;

    private Texture texture;
    private Texture depthtexture;

    public Drawbuffer(Shaders shader, String textureName, int width, int height, int imageFilter, int internalTextureFormat) throws Exception {
    	SHADOW_MAP_WIDTH = width;
    	SHADOW_MAP_HEIGHT = height;
        colorbuffer = glGenFramebuffers();
        
        texture = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, textureName, imageFilter, internalTextureFormat);

        glBindFramebuffer(GL_FRAMEBUFFER, colorbuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture.getId(), 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        //this.texture.bindAsUniform(shader.getShaderProgram());
    }
    
    public Drawbuffer(String colortextureName, String depthtextureName, int width, int height) throws Exception {
    	SHADOW_MAP_WIDTH = width;
    	SHADOW_MAP_HEIGHT = height;
        colorbuffer = glGenFramebuffers();
        //depthbuffer = GL30.glGenRenderbuffers();

        texture = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, colortextureName, GL_LINEAR, GL11.GL_RGBA);
        depthtexture = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, depthtextureName, GL_NEAREST, GL11.GL_DEPTH_COMPONENT);

        glBindFramebuffer(GL_FRAMEBUFFER, colorbuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthtexture.getId(), 0);
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


    public Texture getColorMapTexture() {
        return texture;
    }
    
    public Texture getDepthMapTexture() {
        return depthtexture;
    }
 

    public int getColorMapFBO() {
        return colorbuffer;
    }

    public int getDepthMapFBO() {
        return depthbuffer;
    }
    
    public void cleanup() {
        glDeleteFramebuffers(colorbuffer);
        glDeleteRenderbuffers(depthbuffer);
        texture.cleanup();
    }
    
    public Drawbuffer getThis() {
    	return this;
    }
}