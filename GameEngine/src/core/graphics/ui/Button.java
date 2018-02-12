package core.graphics.ui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import core.graphics.renderUtils.Shaders;
import core.utils.math.Matrix4f;
import core.utils.math.Vector2f;
import core.utils.other.Texture;
import core.input.Mouse;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Button implements UIElement {
	Vector2f position;
	int width = 100;
	int height = 100;
	positions positionStatus;
	
	int vbo;
	public Shaders shader;
	private Runnable action;

	public Button(Runnable run, Texture buttonTexture) {
		this.position = new Vector2f();
		this.init(run, buttonTexture);
	}
	
	public Button(Vector2f position, Runnable run, Texture buttonTexture) {
		this.position = position;
		this.init(run, buttonTexture);
	}
	
	public Button(Vector2f position, Runnable run, positions pos, Texture buttonTexture) {
		this.init(run, buttonTexture);
		this.positionStatus = pos;
		this.setPosition(position);
	}
	
	private void init (Runnable run, Texture buttonTexture) {
		this.width = buttonTexture.getWidth();
		this.height = buttonTexture.getHeight();
		this.action = run;
		this.positionStatus = positions.ABSOLUTE;
		this.genButton();
	}
	
	public void setShader(Shaders shader) {
		this.shader = shader;
	}

	@Override
	public void render(Matrix4f mat) {
		glUseProgram(shader.getShaderProgram());
		mat.setIdentity();
		mat.translate(this.position);
		mat.createUniform(shader.getShaderProgram());
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.BYTES * 2, 0);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void mouseCollision(Vector2f mouse) {
		if (mouse.x >= this.position.x &&  mouse.x <= (this.position.x+this.width)) {
			if (mouse.y >= this.position.y && mouse.y <= (this.position.y + this.height)) {
				this.onPress();
			}
		}
	}

	@Override
	public void onPress() {
		this.action.run();
	}

	public void setPosition(Vector2f v) {
		if (positionStatus == positions.ABSOLUTE) {
			this.position.x = v.x;
			this.position.y = v.y;
		} else {
			System.err.println("could not set position. Position status: " + this.positionStatus.toString());
		}
	}

	public void setPositionStatus(positions p) {
		this.positionStatus = p;
		
		if (this.positionStatus  == positions.CENTERED) {
			
		}
	}

	private void genButton() {
		this.vbo = glGenBuffers();
		FloatBuffer data = BufferUtils.createFloatBuffer(27);
		data.put(new float[] { 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0 });
		data.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public enum positions {
		ABSOLUTE, RELATIVE, CENTERED, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}
}
