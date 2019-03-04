package core.engine;

import static org.lwjgl.glfw.GLFW.*;


import core.graphics.lights.DirectionalLight;
import core.graphics.misc.Color;
import core.graphics.models.ModelBlueprint;
import core.graphics.models.ModelCompiler;
import core.graphics.models.Pawn;
import core.graphics.renderUtils.Camera;
import core.graphics.renderUtils.Quad;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.renderUtils.uniforms.UniformBufferMultiSource;
import core.graphics.renderUtils.uniforms.UniformBufferObject;
import core.graphics.renderUtils.uniforms.UniformBufferSource;
import core.graphics.renderUtils.uniforms.*;
import core.graphics.renderUtils.uniforms.UniformBufferObject.glVariableType;
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
import static org.lwjgl.opengl.GL31.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import core.graphics.ui.InputPointer;
import core.graphics.ui.UiPanel;
import core.graphics.ui.old.MenuSystem;
import core.graphics.ui.old.UIPanel;

import java.util.function.BiFunction;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class Engine {
	static Window window;
	private Keyboard keyboard;
	private Mouse mouse;
	private Shaders dynamicShadows, staticShadows;
	private Shaders quad;
	private Pawn player;
	
	//Matrix4f transformMatrix;
	DirectionalLight sun;
	
	ShadowMap staticShadowMap;
	ShadowMap dynamicShadowMap;
	
	Quad screenQuad;
	
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
		
		// Create keyboard and mouse.
		keyboard = new Keyboard(window);
		mouse = new Mouse(window, 0.5f);
		
		this.staticRenderStack = new ArrayList<RenderObject>();
		this.dynamicRenderStack = new ArrayList<RenderObject>();

		// Enable attribute arrays.
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		try {
			Shaders.addImport("/Assets/Shaders/Imports/imports.shd");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] sunNames = new String[]{"position","diffuse","ambient","specular"};
		sun = new DirectionalLight(new Vector4f(0.2f,1,0.5f,0), new Vector4f(1f,1f,1f,1), window.getSkyColor().asMultiplied(0.4f), new Vector4f(2,2,2,0), "Light", sunNames);

		
		// Init uniforms
		UniformBufferObject uniform = new UniformBufferObject("Matrices", GL_DYNAMIC_DRAW);

		UniformBufferMultiSource camUniform = new UniformBufferMultiSource(glVariableType.MATRIX4F, "camera", "viewMatrix");
		camUniform.bindToBufferObject(uniform);
		
		UniformBufferSource translateMatrix = new UniformBufferSource("translateMatrix", glVariableType.MATRIX4F);
		translateMatrix.bindToBufferObject(uniform);
		
		UniformBufferMultiSource staticUniform = new UniformBufferMultiSource(glVariableType.MATRIX4F, "staticOrthoMatrix");
		staticUniform.bindToBufferObject(uniform);
		
		UniformBufferMultiSource dynamicUniform = new UniformBufferMultiSource(glVariableType.MATRIX4F, "dynamicOrthoMatrix");
		dynamicUniform.bindToBufferObject(uniform);
		
		try {
			uniform.finalizeBuffer();
			
		} catch (ShaderCompileException e) {
			e.printStackTrace();
		}
		
		
		// Create shaders for shadowmapping. 
		dynamicShadows = new Shaders("/Assets/Shaders/Shadows/Default/dynamic.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		staticShadows = new Shaders("/Assets/Shaders/Shadows/Default/static.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		
		try {
			staticShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"staticShadowmap", 1080*3, 1080*3, GL_NEAREST, new Vector4f(40,40,-100,100), staticUniform);
			dynamicShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"dynamicShadowmap", 2048, 2048, GL_NEAREST, new Vector4f(7,7,-100,100), dynamicUniform);
		} catch (Exception e) {
			e.printStackTrace();
		}
		staticShadowMap.bindToLight(sun);
		dynamicShadowMap.bindToLight(sun);
		



		// Create player.
		this.addPlayer(camUniform);
		this.mouse.addListener(this.player);
		player.getModel().setDepthShader(this.dynamicShadows);

		// Create scene.
		ModelBlueprint m = null;
		try {
			m = ModelCompiler.loadModelBlueprint("/Assets/Models/Env/Maps/map.ini");
		} catch (IOException | ShaderCompileException e) {

			e.printStackTrace();
		}
		
		m.setDepthShader(this.staticShadows);
		this.dynamicShadowMap.bindPositionTo(player.getPosition());
		
		// add models to renderStack.
		this.dynamicRenderStack.add(player.getModel());
		this.staticRenderStack.add(m);
		

		// bind models to global transform matrix.
		this.player.getModel().bindTransformMatrix(translateMatrix);
		m.bindTransformMatrix(translateMatrix);
		
		// Create deffered shader and quad.
		Shaders deffered = new Shaders("/Assets/Shaders/deffered/deffered.vert","/Assets/Shaders/deffered/deffered.frag");
		screenQuad = new Quad(window.getWidth(),window.getHeight(),deffered);
		
		// bind shadow textures to the deffered pipeline.
		this.staticShadowMap.getTexture().bindAsUniforms( deffered);
		this.dynamicShadowMap.getTexture().bindAsUniforms(deffered);

		
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
			
			this.dynamicShadowMap.updateCameraUniform();
			window.renderTextured(this.dynamicRenderStack, this.staticRenderStack);
			//glUseProgram(0);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			window.prepareToRender();
			screenQuad.drawQuad();
			
			window.endRender();			
		}
	}
	
	public void addPlayer(UniformBufferMultiSource camUniform) {
		player = new Pawn();
		
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
		
		Camera cam = new Camera(new Vector3f(0, 1, 0), new Vector3f(-1,0,0), 45, (float)window.getWidth()/window.getHeight(), 0.1f, 200f, Camera.updateType.BOTH, camUniform);
		player.bindCamera(cam);
		
		player.thirdPersonPreset(0.35f, new Vector3f(0,2,0), 10,5);
		cam.bindFocusPos(player.getPosition());
	}

}
