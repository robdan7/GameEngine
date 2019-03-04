package core.engine;

import static org.lwjgl.glfw.GLFW.*;


import core.graphics.lights.DirectionalLight;
import core.graphics.misc.Color;
import core.graphics.models.ModelBlueprint;
import core.graphics.models.ModelCompiler;
import core.graphics.models.Pawn;
import core.graphics.renderUtils.Quad;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.renderUtils.uniforms.UniformBufferMultiSource;
import core.graphics.renderUtils.uniforms.UniformBufferObject;
import core.graphics.renderUtils.uniforms.UniformBufferSource;
import core.graphics.renderUtils.uniforms.old.UniformObject;
import core.graphics.renderUtils.uniforms.old.UniformSource;
import core.input.Key;
import core.input.Keyboard;
import core.input.Mouse;
import core.input.MouseListener;
import core.input.MouseObserver;
import core.utils.event.Observer;
import core.utils.math.Matrix4f;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.util.ArrayList;

import core.graphics.ui.InputPointer;
import core.graphics.ui.UiPanel;
import core.graphics.ui.old.MenuSystem;
import core.graphics.ui.old.UIPanel;

import java.util.function.BiFunction;

public class Engine {
	static Window window;
	private Keyboard keyboard;
	private Mouse mouse;
	private Shaders shader;
	private Shaders dynamicShadows, staticShadows;
	private Shaders quad;
	private Pawn player;
	
	//Matrix4f transformMatrix;
	DirectionalLight sun;
	
	ShadowMap staticShadowMap;
	ShadowMap dynamicShadowMap;
	
	@Deprecated
	MenuSystem menu;
	
	
	/**
	 * List with static render models.
	 */
	ArrayList<RenderObject> staticRenderStack;
	
	/**
	 * List with dynamic render models.
	 */
	ArrayList<RenderObject> dynamicRenderStack;
	
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
		/*
		UniformBufferObject obj = new UniformBufferObject("object", 0);
		
		UniformBufferSource src1 = new UniformBufferSource("mat1",UniformBufferObject.glVariableType.MATRIX4F);
		UniformBufferSource src2 = new UniformBufferSource("vector1",UniformBufferObject.glVariableType.VEC3);
		UniformBufferSource src3 = new UniformBufferSource("vector2",UniformBufferObject.glVariableType.VEC4);
		
		UniformBufferMultiSource src4 = new UniformBufferMultiSource(UniformBufferObject.glVariableType.MATRIX4F, "buf","buf2","buf3");
		
		src1.bindToBufferObject(obj);
		src2.bindToBufferObject(obj);
		src4.bindToBufferObject(obj);
		src3.bindToBufferObject(obj);
		
		try {
			obj.finalize();
		} catch (ShaderCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(obj.uniformCode);
		*/
		
	}
	
	private void run() {
		window = new Window(1080,720);
		window.setSkyColor(Color.rgbToPercent(110,148,214,255));
		
		keyboard = new Keyboard(window);
		mouse = new Mouse(window, 0.5f);
		
		this.staticRenderStack = new ArrayList<RenderObject>();
		this.dynamicRenderStack = new ArrayList<RenderObject>();

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		try {
			Shaders.addImport("/Assets/Shaders/Imports/imports.shd");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sun = new DirectionalLight(new Vector4f(0.2f,1,0.5f,0), new Vector4f(1f,1f,1f,1), window.getSkyColor().asMultiplied(0.4f), new Vector4f(2,2,2,0), "/Assets/Shaders/Uniforms/light.unf");
		UniformObject uniform = new UniformObject("/Assets/Shaders/Uniforms/matrices.unf", GL_DYNAMIC_DRAW);
		shader = new Shaders("/Assets/Shaders/Deafult/shader.vert", "/Assets/Shaders/Deafult/shader.frag");
		dynamicShadows = new Shaders("/Assets/Shaders/Shadows/Default/dynamic.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		staticShadows = new Shaders("/Assets/Shaders/Shadows/Default/static.vert", "/Assets/Shaders/Shadows/Default/shader.frag");

		try {
			staticShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"staticShadowmap", 1080*3, 1080*3, GL_NEAREST, new Vector4f(40,40,-100,100));
			dynamicShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"dynamicShadowmap", 2048, 2048, GL_NEAREST, new Vector4f(7,7,-100,100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		staticShadowMap.bindToLight(sun);
		dynamicShadowMap.bindToLight(sun);

		
		this.addPlayer();
		//ModelBlueprint m = new ModelBlueprint("/Assets/Models/Env/Maps/Stock_Terrain.obj", "modelTexture");
		ModelBlueprint m = null;
		try {
			m = ModelCompiler.loadModelBlueprint("/Assets/Models/Env/Maps/map.ini");
		} catch (IOException | ShaderCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//m.bindTexture(this.shader);
		
		//player.getModel().setShader(shader);
		player.getModel().setDepthShader(this.dynamicShadows);
		//m.setShader(shader);
		m.setDepthShader(this.staticShadows);
		this.dynamicShadowMap.bindPositionTo(player.getPosition());
		
		//window.addDynamicRenderObject(player.getModel());
		this.dynamicRenderStack.add(player.getModel());
		this.staticRenderStack.add(m);
		//window.addStaticRenderObject(m);

		this.mouse.addListener(this.player);
		
		UniformSource translateMatrix = new UniformSource(Matrix4f.SIZE);
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
		//menu = new MenuSystem(this.window,2);
		//this.mouse.addListener(menu);
		
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
		//window.renderStaticShadowMap(this.staticShadowMap);
		// Render static shadows.
		window.renderShadowMap(this.staticShadowMap, this.staticRenderStack);
		
		//UiPanel.switchPanel("");
		
		/*
		UiPanel.init("hello");
		

		InputPointer p = new InputPointer((Observer<Object, MouseObserver, MouseListener> t, Object u) -> UiPanel.getActive());
		
		this.mouse.addListener(p);
		
		UiPanel.switchPanel("world");
*/
		while (!this.shouldClose && !glfwWindowShouldClose(window.getWindow())) {
			// Get all window events. This is where key callback is activated.
			glfwPollEvents();			
	
			player.updateMovement();

			window.renderShadowMap(this.dynamicShadowMap, this.dynamicRenderStack);
			glBindFramebuffer(GL_FRAMEBUFFER, screenQuad.getFBO());
			glClearColor(0, 0, 0, 0); // Set the background to transparent.
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			
			window.renderTextured(this.dynamicShadowMap, this.dynamicRenderStack, this.staticRenderStack);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			window.prepareToRender();
			screenQuad.drawQuad();
			
			window.endRender();			
		}
	}
	
	public void addPlayer() {
		player = new Pawn(new Vector3f(0, 1, 0), new Vector3f(-1,0,0), 45, (float)window.getWidth()/window.getHeight(), 0.1f, 200f);
		
		keyboard.addKeyPressFunction(GLFW_KEY_W,() -> player.addZvelocity(1)); 
		keyboard.addKeyReleaseFunction(GLFW_KEY_W, () -> player.addZvelocity(-1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_S,() -> player.addZvelocity(-1));
		keyboard.addKeyReleaseFunction(GLFW_KEY_S, () -> player.addZvelocity(1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_A,() -> player.addXvelocity(1));
		keyboard.addKeyReleaseFunction(GLFW_KEY_A, () -> player.addXvelocity(-1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_D, () -> player.addXvelocity(-1));
		keyboard.addKeyReleaseFunction(GLFW_KEY_D, () -> player.addXvelocity(1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_SPACE, () -> player.addYvelocity(1));
		keyboard.addKeyReleaseFunction(GLFW_KEY_SPACE, () -> player.addYvelocity(-1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_LEFT_SHIFT, () -> player.addYvelocity(-1));
		keyboard.addKeyReleaseFunction(GLFW_KEY_LEFT_SHIFT, () -> player.addYvelocity(1));
		
		keyboard.addKeyPressFunction(GLFW_KEY_ESCAPE, () -> mouse.toggleGrab());
		
		try {
			player.addModel(ModelCompiler.loadModelBlueprint("/Assets/Models/temp.ini"));
		} catch (IOException | ShaderCompileException e) {
			e.printStackTrace();
		}
		player.thirdPersonPreset(0.35f, new Vector3f(0,2,0), 10,5);
		player.bindTexture(this.shader);
	}

}
