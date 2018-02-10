package engine;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import input.Keyboard;
import input.Mouse;
import objects.pawn.*;
import ui.MenuSystem;
import ui.UIPanel;
import objects.lights.DirectionalLight;
import objects.lights.Light;
import objects.models.*;
import utils.math.Matrix4f;
import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import utils.rendering.Shaders;
import utils.rendering.ShadowMap;


public class Engine {
	static Window window;
	private Keyboard keyboard;
	Mouse mouse;
	Shaders shader;
	Shaders shadowShader;
	Shaders quad;
	Pawn player;
	
	Matrix4f transformMatrix;
	DirectionalLight sun;
	
	ShadowMap staticShadowMap;
	ShadowMap dynamicShadowMap;
	
	MenuSystem menu;
	
	boolean shouldClose = false;
	
	public Engine() {
		run();
		shader.dispose();
		shadowShader.dispose();
		window.deleteAllRenderObjects();
		window.deleteWindow();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Engine();
	}
	
	private void run() {
		window = new Window(1080,720);
		//window.enableVertexArray();
		//window.enableNormals();
		//window.enableTexture();
		glClearColor(01.0f, 01.0f, 01.0f, 01.0f);
		keyboard = new Keyboard(window);
		mouse = new Mouse(window, 0.5f);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		shader = new Shaders("/Core/Shaders/Deafult/shader.vert", "/Core/Shaders/Deafult/shader.frag", true);
		shadowShader = new Shaders("/Core/Shaders/Shadows/Default/shader.vert", "/Core/Shaders/Shadows/Default/shader.frag", true);

		this.addPlayer();
		
		
		transformMatrix = new Matrix4f("translateMatrix");
		
		//glUseProgram(shader.getShaderProgram());
		
		

		sun = new DirectionalLight(new Vector4f(0,1,0.1f,0), new Vector4f(1,1,1,1), new Vector4f(0.5f,0.5f,0.5f,1), 0);

		try {
			staticShadowMap = new ShadowMap(shader, "staticShadowmap", "staticOrthoMatrix", window.getWidth()*5, window.getHeight()*5, GL_LINEAR, new Vector4f(50,50,-100,100));
			dynamicShadowMap = new ShadowMap(shader, "dynamicShadowmap", "dynamicOrthoMatrix", window.getWidth()*4, window.getHeight()*4, GL_LINEAR, new Vector4f(10,10,-100,100));
			ShadowMap.setSharedMatrixName("orthomatrix");
			//shadowMap.getBuffer().getColorMapTexture().bindAsUniform(this.shader.getShaderProgram());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticShadowMap.bindToLight(sun);
		dynamicShadowMap.bindToLight(sun);
		dynamicShadowMap.updateCameraUniform(this.shader.getShaderProgram());
		staticShadowMap.updateCameraUniform(this.shader.getShaderProgram());
		

		ModelBlueprint m = new ModelBlueprint("/Core/Models/Env/Maps/Stock_Terrain.obj", "modelTexture");
		m.bindTexture(this.shader);
		
		player.setShader(shader);
		player.setDepthShader(this.shadowShader);
		m.setShader(shader);
		m.setDepthShader(this.shadowShader);
		this.dynamicShadowMap.bindPositionTo(player.getPosition());
		
		window.addDynamicRenderObject(player);
		window.addStaticRenderObject(m);

		this.setupUI();
		this.renderloop();
		m.discard();
	}
	
	public void setupUI() {
		menu = new MenuSystem(this.mouse, window.getWidth(),window.getHeight(),2);
		
		menu.addPanel("settings");
		menu.setActivePanel("settings");
		menu.getActivePanel().addShader("/Core/Shaders/UI/button.vert","/Core/Shaders/UI/button.frag", UIPanel.Elements.BUTTON);
		menu.getActivePanel().addTexture("/Core/Textures/button.png", UIPanel.Elements.BUTTON);
		
		menu.getActivePanel().addButton(new Vector2f(400,280), () -> menu.setActivePanel("menu"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
		
		menu.addPanel("menu");
		menu.setActivePanel("menu");
		
		menu.getActivePanel().addShader("/Core/Shaders/UI/button.vert","/Core/Shaders/UI/button.frag", UIPanel.Elements.BUTTON);
		menu.getActivePanel().addTexture("/Core/Textures/button.png", UIPanel.Elements.BUTTON);
		
		
		menu.getActivePanel().addButton(new Vector2f(-1080/2,-360), () -> System.out.println("hej"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
		menu.getActivePanel().addButton(new Vector2f(-540,280), () -> menu.setActivePanel("settings"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
	}
	
	public void renderloop() {
		staticShadowMap.getTexture().bindAsUniform(shader.getShaderProgram());
		this.dynamicShadowMap.getTexture().bindAsUniform(this.shader.getShaderProgram());
		window.renderStaticShadowMap(this.transformMatrix, this.staticShadowMap);
		
		while (!this.shouldClose && !glfwWindowShouldClose(window.getWindow())) {
			player.resetMovement();
			((Keyboard)keyboard).getInput();
			if (!mouse.isVisible()) {
				player.rotateCamera(-mouse.getDX(),-mouse.getDY());
			}
			player.updateMovement(player.getCamera().gethAngle());
			player.updateCamera(shader);
			
			window.prepareToRender();
			window.renderDynamicShadowMap(this.transformMatrix, this.dynamicShadowMap);
			window.renderTextured(this.transformMatrix, this.dynamicShadowMap);
			menu.render(this.transformMatrix);
			window.endRender();

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	
	public void addPlayer() {
		player = new Pawn(new Vector3f(0, 1, 0), 60, (float)window.getWidth()/window.getHeight(), 0.1f, 200f, "camera");
		keyboard.addKeyFunction(GLFW_KEY_W, () -> player.setZvelocity(1)); 
		keyboard.addKeyFunction(GLFW_KEY_S, () -> player.setZvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_A, () -> player.setXvelocity(1));
		keyboard.addKeyFunction(GLFW_KEY_D, () -> player.setXvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_SPACE, () -> player.setYvelocity(1));
		keyboard.addKeyFunction(GLFW.GLFW_KEY_LEFT_SHIFT, () -> player.setYvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_ESCAPE, true, () -> mouse.toggleGrab(window));
		player.addModel("/Core/Models/stock.obj", "modelTexture");
		player.thirdPersonPreset(0.25f, new Vector3f(0,70,0), 10);
	}

}
