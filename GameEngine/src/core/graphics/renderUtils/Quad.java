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

import core.engine.Window;
import core.graphics.renderUtils.buffers.Gbuffer;

public class Quad {
	private Gbuffer map;
	Shaders defferedShader;
	int vbo;
	/*
	public void loadingScreen() {
		Texture texture = new Texture("/res/coverImage.png", "texturetest");
		//int shader = Shaders.createShadersProgram("/Shaders/loadingScreen/load.vert", "/Shaders/loadingScreen/load.frag", true);
		texture.bindAsUniform(shader);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL20.glUseProgram(shader);
		//GL20.glUseProgram(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL20.glUseProgram(0);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}*/
	/*
	public void resize(int width , int height) {
		map.cleanup();
		try {
			//map = new Drawbuffer("inTexture", "inDepth", Display.getWidth(), Display.getHeight());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.getColorMapTexture().bindAsUniform(defferedShader);
		//map.getColorMapTexture().bindAsUniform(Main.cloudshaderProgram);
		map.getDepthMapTexture().bindAsUniform(defferedShader);
	}*/
	
	public Quad(int width, int height, Shaders shader) {
		defferedShader = shader;
		glUseProgram(defferedShader.getShaderProgram());
		String[] colorTextures = {"colorBuffer", "normalBuffer", "positionBuffer"};
		String depthDextures = "inDepth";
		int[] formats = {GL_RGBA,GL_RGB,GL_RGBA32F};
		int[] baseFormats = {GL_RGBA,GL_RGB,GL_RGBA};
		int depthFormat = GL_DEPTH_COMPONENT;
		try {
			map = new Gbuffer(width, height, GL_LINEAR, colorTextures,formats, baseFormats);
			map.attachDepthTexture(depthDextures, depthFormat);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		map.bindAllTextures(defferedShader);
		vbo = genBuffer();
		glUseProgram(0);
	}
	
	public int getFBO() {
		return this.map.getColorMapFBO();
	}
	
	public void drawQuad() {
		GL20.glUseProgram(defferedShader.getShaderProgram());
		glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
		//GL20.glUseProgram(0);
		GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*3, 0);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
}
