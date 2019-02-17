package core.engine;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.opengl.GL20;

import core.graphics.lights.DirectionalLight;
import core.graphics.misc.Color;
import core.graphics.models.ModelBlueprint;
import core.graphics.models.ModelCompiler;
import core.graphics.models.Pawn;
import core.graphics.renderUtils.Quad;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.renderUtils.uniforms.UniformObject;
import core.graphics.renderUtils.uniforms.UniformSource;
import core.input.Keyboard;
import core.input.Mouse;
import core.utils.math.Line;
import core.utils.math.Matrix4f;
import core.utils.math.Plane;
import core.utils.math.Vector;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;
import core.utils.other.BufferTools;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import core.graphics.ui.*;
import core.graphics.ui.old.MenuSystem;
import core.graphics.ui.old.UIPanel;


public class Engine {
	static Window window;
	private Keyboard keyboard;
	Mouse mouse;
	Shaders shader;
	Shaders dynamicShadows, staticShadows;
	Shaders quad;
	Pawn player;
	
	Matrix4f transformMatrix;
	DirectionalLight sun;
	
	ShadowMap staticShadowMap;
	ShadowMap dynamicShadowMap;
	
	@Deprecated
	MenuSystem menu;
	
	boolean shouldClose = false;
	
	public Engine() {
		
		run();
		shader.dispose();
		dynamicShadows.dispose();
		staticShadows.dispose();
		window.deleteAllRenderObjects();
		window.deleteWindow();
		
	}

	public static void main(String[] args) {
		new Engine();
	}
	
	private void run() {
		window = new Window(1080,720);
		window.setSkyColor(Color.rgbToPercent(110,148,214,255));
		
		keyboard = new Keyboard(window);
		mouse = new Mouse(window, 0.5f);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		try {
			Shaders.addImport("/Assets/Shaders/Imports/imports.shd");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sun = new DirectionalLight(new Vector4f(0.2f,1,0.5f,0), new Vector4f(1f,1f,1f,1), Vector.multiply(window.getSkyColor(), 0.4f), new Vector4f(2,2,2,0), 0, "/Assets/Shaders/Uniforms/light.unf");
		UniformObject uniform = new UniformObject("/Assets/Shaders/Uniforms/matrices.unf", GL_DYNAMIC_DRAW);
		shader = new Shaders("/Assets/Shaders/Deafult/shader.vert", "/Assets/Shaders/Deafult/shader.frag");
		dynamicShadows = new Shaders("/Assets/Shaders/Shadows/Default/dynamic.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		staticShadows = new Shaders("/Assets/Shaders/Shadows/Default/static.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		
		transformMatrix = new Matrix4f("translateMatrix");
		
		//glUseProgram(shader.getShaderProgram());
		

		try {
			staticShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"staticShadowmap", 1080*3, 1080*3, GL_NEAREST, new Vector4f(10,10,-100,100));
			dynamicShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"dynamicShadowmap", 2048, 2048, GL_NEAREST, new Vector4f(7,7,-100,100));
			//shadowMap.getBuffer().getColorMapTexture().bindAsUniform(this.shader.getShaderProgram());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticShadowMap.bindToLight(sun);
		dynamicShadowMap.bindToLight(sun);
		//GL20.glUseProgram(this.shader.getShaderProgram());
		
		
		this.addPlayer();
		//ModelBlueprint m = new ModelBlueprint("/Assets/Models/Env/Maps/Stock_Terrain.obj", "modelTexture");
		ModelBlueprint m = null;
		try {
			m = ModelCompiler.loadModelBlueprint("/Assets/Models/Demo/demo.ini");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//m.bindTexture(this.shader);
		
		//player.getModel().setShader(shader);
		player.getModel().setDepthShader(this.dynamicShadows);
		//m.setShader(shader);
		m.setDepthShader(this.staticShadows);
		this.dynamicShadowMap.bindPositionTo(player.getPosition());
		
		window.addDynamicRenderObject(player.getModel());
		window.addStaticRenderObject(m);

		this.mouse.addListener(this.player);
		
		UniformSource translateMatrix = new UniformSource(Matrix4f.getSize());
		player.getCamera().bindToUniformObject(uniform);
		translateMatrix.bindToUniformObject(uniform);
		
		dynamicShadowMap.getCamera().bindToUniformObject(uniform);
		staticShadowMap.getCamera().bindToUniformObject(uniform);
		//uniform.createUniformBlock(player.getCamera(), translateMatrix, staticShadowMap.getCamera(), dynamicShadowMap.getCamera());
		//player.getCamera().getLookAtMatrix().lookAt(new Vector3f(0,0,-10), new Vector3f(0,0,1), new Vector3f(0,1,0), new Vector3f(-1,0,0));
		//player.setPosition(new Vector3f(0,3,0));

		this.player.rotateCamera((float)Math.PI, 0);
		//this.player.updateMovement();
		
		this.player.getModel().bindTransformMatrix(translateMatrix);
		m.bindTransformMatrix(translateMatrix);
		//this.setupUI();
		this.renderloop();
		m.discard();
	}
	
	@Deprecated
	public void setupUI() {
		menu = new MenuSystem(this.window,2);
		this.mouse.addListener(menu);
		
		menu.addPanel("settings");
		menu.setActivePanel("settings");
		menu.getActivePanel().addShader("/Assets/Shaders/UI/button.vert","/Assets/Shaders/UI/button.frag", UIPanel.Elements.BUTTON);
		menu.getActivePanel().addTexture("/Assets/Textures/button.png", UIPanel.Elements.BUTTON);
		
		menu.getActivePanel().addButton(new Vector2f(200,180), () -> menu.setActivePanel("menu"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
		
		menu.addPanel("menu");
		menu.setActivePanel("menu");
		
		menu.getActivePanel().addShader("/Assets/Shaders/UI/button.vert","/Assets/Shaders/UI/button.frag", UIPanel.Elements.BUTTON);
		menu.getActivePanel().addTexture("/Assets/Textures/button.png", UIPanel.Elements.BUTTON);
		
		
		menu.getActivePanel().addButton(new Vector2f(-1080/2,-360), () -> System.out.println("hej"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
		menu.getActivePanel().addButton(new Vector2f(-540,180), () -> menu.setActivePanel("settings"), menu.getActivePanel().getTexture(UIPanel.Elements.BUTTON));
	}
	
	public void renderloop() {
		Shaders deffered = new Shaders("/Assets/Shaders/deffered/deffered.vert","/Assets/Shaders/deffered/deffered.frag");
		Quad screenQuad = new Quad(window.getWidth(),window.getHeight(),deffered);
		

		this.staticShadowMap.getTexture().bindAsUniforms(shader, deffered);
		this.dynamicShadowMap.getTexture().bindAsUniforms(this.shader,deffered);
		window.renderStaticShadowMap(this.staticShadowMap);
		
		
		while (!this.shouldClose && !glfwWindowShouldClose(window.getWindow())) {
			player.resetMovement();
			keyboard.getInput();
			if (!mouse.isVisible()) {
				player.rotateCamera(-mouse.getDX(),-mouse.getDY());
			}
			player.updateMovement();
			//player.getCamera().updateUniform();
			
			

			window.renderDynamicShadowMap(this.dynamicShadowMap);
			glBindFramebuffer(GL_FRAMEBUFFER, screenQuad.getFBO());
			glClearColor(0, 0, 0, 0); // Set the background to transparent.
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			
			window.renderTextured(this.dynamicShadowMap);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			window.prepareToRender();
			glClearColor(0, 0, 1, 1);
			screenQuad.drawQuad();
			//menu.render(this.transformMatrix);
			window.endRender();

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	
	public void addPlayer() {
		player = new Pawn(new Vector3f(0, 1, 0), new Vector3f(-1,0,0), 45, (float)window.getWidth()/window.getHeight(), 0.1f, 200f);
		keyboard.addKeyFunction(GLFW_KEY_W, () -> player.setZvelocity(1)); 
		keyboard.addKeyFunction(GLFW_KEY_S, () -> player.setZvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_A, () -> player.setXvelocity(1));
		keyboard.addKeyFunction(GLFW_KEY_D, () -> player.setXvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_SPACE, () -> player.setYvelocity(1));
		keyboard.addKeyFunction(GLFW.GLFW_KEY_LEFT_SHIFT, () -> player.setYvelocity(-1));
		keyboard.addKeyFunction(GLFW_KEY_ESCAPE, true, () -> mouse.toggleGrab());
		try {
			player.addModel(ModelCompiler.loadModelBlueprint("/Assets/Models/temp.ini"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.thirdPersonPreset(0.3f, new Vector3f(0,2,0), 10,5);
		player.bindTexture(this.shader);
	}

}
