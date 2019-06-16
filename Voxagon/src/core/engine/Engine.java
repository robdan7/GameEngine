package core.engine;

import static org.lwjgl.glfw.GLFW.*;

import core.graphics.entities.ModelInstance;
import core.graphics.entities.assimp.AssimpScene;
import core.graphics.entities.assimp.assets.BluePrint;
import core.graphics.entities.staticMesh.StaticModel;
import core.graphics.lights.DirectionalLight;
import core.graphics.misc.Color;
import core.graphics.models.Model;
import core.graphics.models.ModelBlueprint;
import core.graphics.models.ModelCompiler;
import core.graphics.models.OBJLoader;
import core.graphics.models.Pawn;
import core.graphics.renderUtils.Camera;
import core.graphics.renderUtils.Framebuffer;
import core.graphics.renderUtils.Quad;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.Shaders.ShaderCompileException;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.renderUtils.uniforms.UniformBufferSource;
import core.graphics.shading.GLSLvariableType;
import core.graphics.shading.Material;
import core.graphics.renderUtils.uniforms.UniformBufferObject;
import core.input.Keyboard;
import core.input.Mouse;
import core.input.MouseListener;
import core.input.MouseObserver;
import core.input.ui.KeyLpointer;
import core.input.ui.MouseLpointer;
import core.input.ui.UiPanel;
import core.physics.collision.BoundingSphere;
import core.physics.collision.CollisionMesh;
import core.physics.collision.CollisionObject;
import core.physics.collision.GJK;
import core.utils.datatypes.GlueList;
import core.utils.event.Observer;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.xml.sax.SAXException;



public class Engine {
	// TODO add custom attributes to shaders that updates with each instance. The player and shadows are deactivated.
	static Window window;
	private Keyboard keyboard;
	private Mouse mouse;
	private Shaders dynamicShadows, staticShadows;
	private Pawn player;
	
	//Matrix4f transformMatrix;
	DirectionalLight sun;
	
	ShadowMap staticShadowMap;
	ShadowMap dynamicShadowMap;
	
	Quad screenQuad;

	
	/**
	 * List with static render models.
	 */
	GlueList<RenderObject> staticRenderStack;
	
	/**
	 * List with dynamic render models.
	 */
	GlueList<RenderObject> dynamicRenderStack;
	
	boolean shouldClose = false;
	
	public Engine() {
		
		run();
		dynamicShadows.dispose();
		staticShadows.dispose();
		//window.deleteAllRenderObjects();
		window.deleteWindow();
		
	}

	public static void main(String[] args) {
		new Engine();		
	}
	
	private void run() {
		window = new Window(1080,720);
		window.setSkyColor(Color.rgbToPercent(110,148,214,255));
		
		// Create keyboard and mouse.
		keyboard = new Keyboard(window);
		mouse = new Mouse(window, 0.5f);
		
		this.staticRenderStack = new GlueList<RenderObject>();
		this.dynamicRenderStack = new GlueList<RenderObject>();

		try {
			Shaders.addImport("/Assets/Shaders/Imports/imports.shd");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] sunNames = new String[]{"position","diffuse","ambient","specular"};
		sun = new DirectionalLight(new Vector4f(0.2f,1,0.5f,0), new Vector4f(1f,1f,1f,1), window.getSkyColor().asMultiplied(0.4f), new Vector4f(2,2,2,0), "Light", sunNames);
		
		// Init uniforms
		UniformBufferObject uniform = new UniformBufferObject("Matrices");

		UniformBufferSource camUniform = new UniformBufferSource(uniform, 0, GLSLvariableType.MAT4, "camera", "viewMatrix");
		
		UniformBufferSource translateMatrix = new UniformBufferSource(uniform, 1, GLSLvariableType.MAT4, "translateMatrix");
		
		UniformBufferSource staticUniform = new UniformBufferSource(uniform, 2, GLSLvariableType.MAT4, "staticOrthoMatrix");
		
		UniformBufferSource dynamicUniform = new UniformBufferSource(uniform, 3, GLSLvariableType.MAT4, "dynamicOrthoMatrix");
		
		try {
			//uniform.finalizeBuffer();
			
		} catch (ShaderCompileException e) {
			e.printStackTrace();
		}
		
		
		// Create shaders for shadowmapping. 
		dynamicShadows = new Shaders("/Assets/Shaders/Shadows/Default/dynamic.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		staticShadows = new Shaders("/Assets/Shaders/Shadows/Default/static.vert", "/Assets/Shaders/Shadows/Default/shader.frag");
		
		try {
			staticShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"staticShadowmap", 1080*4, 1080*4, GL_NEAREST, new Vector4f(40,40,-100,100), staticUniform);
			dynamicShadowMap = new ShadowMap(new Vector3f(0,1,0), new Vector3f(-1,0,0),"dynamicShadowmap", 2048, 2048, GL_NEAREST, new Vector4f(7,7,-100,100), dynamicUniform);
		} catch (Exception e) {
			e.printStackTrace();
		}
		staticShadowMap.bindToLight(sun);
		dynamicShadowMap.bindToLight(sun);
		



		// Create player.
		this.addPlayer(camUniform);
		this.addMenuSystem();
		UiPanel playerPanel = this.addPlayerKeys();
		UiPanel.getActive().setHideAction(() -> UiPanel.getActive().switchPanel(playerPanel));
		

		//this.keyboard.addListener(l);
		
		//this.mouse.addListener(this.player);
		player.setDepthShader(this.dynamicShadows);

		// Create scene.
		/*
		ModelBlueprint m = null;
		try {
			m = ModelCompiler.loadModelBlueprint("/Assets/Models/Env/Maps/map.ini");
		} catch (IOException | ShaderCompileException e) {

			e.printStackTrace();
		}		
		m.setDepthShader(this.staticShadows);
		*/
		this.dynamicShadowMap.bindPositionTo(player.getPosition());
		

		// add models to renderStack.
		//this.dynamicRenderStack.add(player);
		//this.dynamicRenderStack.add(test);
		//this.staticRenderStack.add(m);
		

		// bind models to global transform matrix.
		this.player.bindTransformMatrix(translateMatrix);
		//m.bindTransformMatrix(translateMatrix);
		
		
		// Create deffered shader and quad.
		Shaders deffered = new Shaders("/Assets/Shaders/deffered/deffered.vert","/Assets/Shaders/deffered/deffered.frag");
		screenQuad = new Quad(window.getWidth(),window.getHeight(),deffered);
		
		// bind shadow textures to the deffered pipeline.
		this.staticShadowMap.getTexture().bindAsUniforms( deffered);
		this.dynamicShadowMap.getTexture().bindAsUniforms(deffered);

		
		/* 
		 * Scene used for testing purposes!
		 * */
		//AssimpScene scene = new AssimpScene("/Assets/triangles.dae");
		BluePrint bp = new BluePrint("/Assets/new/assimp/blueprints/test.blp");
		this.renderloop();
		//test.discard();
		//m.discard();
	}
	
	@Deprecated
	public void setupUI() {
		//menu = new MenuSystem(this.window,2);
		//this.mouse.addListener(menu);
		/*
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
		*/
	}
	
	public void renderloop() {
		


		Material mat = new Material("/Assets/new/shaders/deffered/Gbuffer.mtl");

		//core.entities.Model instance2 = null;

			//instance1 = core.entities.Model.createModelInstances("/Assets/new/models/O_cube_instance.mod");
		
		
		
		RenderEngine engine = new RenderEngine(this.window.getWindow());
		engine.addRenderStage(screenQuad.getFBO(), this.dynamicRenderStack, true);
		engine.addRenderStage(this.screenQuad.getFBO(), this.staticRenderStack, false);
		

		//this.staticRenderStack.add(instance2);
		
		Framebuffer buffer = new Framebuffer(0, window.getWidth(), window.getHeight());
		buffer.setClearColor(window.getSkyColor());
		GlueList<RenderObject> list = new GlueList<RenderObject>();
		list.add(screenQuad);
		engine.addRenderStage(buffer, list);
		
		core.graphics.entities.Model instance1 = new StaticModel("/Assets/new/models/O_cube_instance.mod");
		StaticModel instance2 = new StaticModel("/Assets/new/models/O_sphere_instance.mod");
		this.staticRenderStack.add(instance1);
		this.staticRenderStack.add(instance2);
		
		while (!this.shouldClose && !glfwWindowShouldClose(window.getWindow())) {

			player.updateMovement();
			

			
			
			//window.renderShadowMap(this.dynamicShadowMap, this.dynamicRenderStack);
			

			engine.renderAll();
			
			//window.endRender();		
			glfwPollEvents();
		}
	}
	
	private void addPlayer(UniformBufferSource camUniform) {
		player = new Pawn("/Assets/Models/temp.ini");
		/*
		try {
			player.addModel(ModelCompiler.loadModelBlueprint("/Assets/Models/temp.ini"));
		} catch (IOException | ShaderCompileException e) {
			e.printStackTrace();
		}
		*/
		Camera cam = new Camera(new Vector3f(0, 1, 0), new Vector3f(-1,0,0), 45, (float)window.getWidth()/window.getHeight(), 0.1f, 200f, Camera.updateType.BOTH, camUniform);
		
		player.bindCamera(cam);
		
		player.thirdPersonPreset(0.35f, new Vector3f(0,0,10), 3f,0);
		cam.bindFocusPos(player.getPosition());
		player.rotateCamera((float)Math.PI, 0);
		//player.setPosition(new Vector3f(1.2f,0.7f,0));
	}
	
	private void addMenuSystem() {
		UiPanel.init(this.player, "playerUI");
		
		MouseLpointer mousePointer = new MouseLpointer((Observer<Object, MouseObserver, MouseListener> t, Object u) -> UiPanel.getActiveMouseListener());
		KeyLpointer keypointer  = new KeyLpointer(() -> UiPanel.getActiveKeyboardListener());
		
		this.mouse.addListener(mousePointer);
		this.keyboard.addListener(keypointer);
		
		UiPanel.getActive().addKeyPressFunction(GLFW_KEY_ESCAPE, () -> UiPanel.getActive().hide());
		
		//mouse.toggleGrab();
	}
	
	private UiPanel addPlayerKeys() {
		UiPanel panel = new UiPanel(player, "");
		
		panel.addKeyPressFunction(GLFW_KEY_W,() -> player.addZvelocity(1)); 
		panel.addKeyReleaseFunction(GLFW_KEY_W, () -> player.addZvelocity(-1));
		
		panel.addKeyPressFunction(GLFW_KEY_S,() -> player.addZvelocity(-1));
		panel.addKeyReleaseFunction(GLFW_KEY_S, () -> player.addZvelocity(1));
		
		panel.addKeyPressFunction(GLFW_KEY_A,() -> player.addXvelocity(1));
		panel.addKeyReleaseFunction(GLFW_KEY_A, () -> player.addXvelocity(-1));
		
		panel.addKeyPressFunction(GLFW_KEY_D, () -> player.addXvelocity(-1));
		panel.addKeyReleaseFunction(GLFW_KEY_D, () -> player.addXvelocity(1));
		
		panel.addKeyPressFunction(GLFW_KEY_SPACE, () -> player.addYvelocity(1));
		panel.addKeyReleaseFunction(GLFW_KEY_SPACE, () -> player.addYvelocity(-1));
		
		panel.addKeyPressFunction(GLFW_KEY_LEFT_SHIFT, () -> player.addYvelocity(-1));
		panel.addKeyReleaseFunction(GLFW_KEY_LEFT_SHIFT, () -> player.addYvelocity(1));
		
		panel.addKeyPressFunction(GLFW_KEY_ESCAPE, () -> UiPanel.popPanel());
		
		panel.addKeyPressFunction(GLFW_KEY_ENTER, ()-> window.renderShadowMap(this.staticShadowMap, this.staticRenderStack));
		
		panel.setHideAction(() -> mouse.showCursor(window));
		panel.setShowAction(() -> mouse.grabCursor(window));
		
		return panel;
	}
}
