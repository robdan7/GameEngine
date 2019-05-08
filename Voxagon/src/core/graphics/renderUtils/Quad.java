package core.graphics.renderUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import core.utils.other.BufferTools;

public class Quad implements RenderObject{
	private Framebuffer framebuffer;
	Shaders defferedShader;
	int vbo;
	int vao;
	
	public Quad(int width, int height, Shaders shader) {
		this.defferedShader = shader;
		// TODO remove the explicit buffers.
		glUseProgram(this.defferedShader.getShaderProgram());
		this.framebuffer = new Framebuffer(width, height);
		this.framebuffer.addTexture("colorBuffer", GL_LINEAR, GL_RGBA, GL_RGBA);
		this.framebuffer.addTexture("normalBuffer", GL_LINEAR, GL30.GL_RGB16F, GL_RGB);
		this.framebuffer.addTexture("positionBuffer", GL_LINEAR, GL_RGBA32F, GL_RGBA);
		this.framebuffer.addDepthAttachment("inDepth", GL_LINEAR, GL_DEPTH_COMPONENT);
		this.framebuffer.completeFramebuffer();
		this.framebuffer.bindTextures(this.defferedShader);
		this.vbo = genBuffer();
		glUseProgram(0);
		
		VertexAttribute color = new VertexAttribute(0,3,GL_FLOAT,false,Float.BYTES*3,0);
		this.vao = BufferTools.createVAO(this.vbo, color);
	}
	
	public Framebuffer getFBO() {
		return this.framebuffer;
	}
	
	public void drawQuad() {
		GL20.glUseProgram(defferedShader.getShaderProgram());
		GL31.glBindVertexArray(this.vao);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL31.glBindVertexArray(0);
	}
	
	private static int genBuffer() {
		int vbo = GL15.glGenBuffers();
		FloatBuffer data = BufferUtils.createFloatBuffer(12);
		data.put(new float[] {-1f,-1f,1f ,1f,-1f,1f, 1f,1f,1, -1f,1f,1});
		data.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		return vbo;
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderTextured() {
		this.drawQuad();
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}


}
