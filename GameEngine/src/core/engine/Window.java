package core.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import core.graphics.misc.Color;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.renderUtils.buffers.Drawbuffer;
import core.utils.math.Matrix4f;
import core.utils.math.Vector;
import core.utils.math.Vector4f;

import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {

	// The window handle
	private long window;
	private int width;
	private int height;
	
	//ArrayList<RenderObject> renderStack;
	ArrayList<RenderObject> staticRenderStack;
	ArrayList<RenderObject> dynamicRenderStack;
	
	private Color skyColor;
	
	public void deleteWindow() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public long getWindow() {
		return window;
	}

	/**
	 * 
	 * @param w - Window width.
	 * @param h - Window height.
	 */
	Window(int w, int h) {
		this.width = w;
		this.height = h;
		this.init();
		this.staticRenderStack = new ArrayList<>();
		this.dynamicRenderStack = new ArrayList<>();
		this.skyColor = new Color();
	}
	
	
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(this.width, this.height, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LEQUAL);
		glCullFace(GL_BACK);
		
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void prepareToRender() {
		glClearColor(skyColor.getR(), skyColor.getG(), skyColor.getB(), skyColor.getAlpha());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	void endRender() {
		glfwSwapBuffers(this.getWindow()); // swap the color buffers
	}
	
	@Deprecated
	void renderStaticShadowMap(ShadowMap shadowmap) {
		shadowmap.updateCameraUniform();
		glCullFace(GL_FRONT);
		glBindFramebuffer(GL_FRAMEBUFFER, shadowmap.getBuffer().getColorMapFBO());
		glViewport(0, 0, shadowmap.getBuffer().getWidth(), shadowmap.getBuffer().getHeight());
		glClear(GL_DEPTH_BUFFER_BIT);
		int lastShader = 0;
		for (int i = 0; i < staticRenderStack.size(); i++) {
			if (lastShader != staticRenderStack.get(i).getDepthShader()) {
				glUseProgram(staticRenderStack.get(i).getDepthShader());
				//shadowmap.updateCameraUniform();
			}
			staticRenderStack.get(i).render();
		}
		glViewport(0, 0, this.getWidth(), this.getHeight());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(0);
		glCullFace(GL_BACK);
	}
	
	@Deprecated
	void renderDynamicShadowMap(ShadowMap shadowMap) {
		shadowMap.updateCameraUniform();
		glCullFace(GL_FRONT);
		glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getBuffer().getColorMapFBO());
		glViewport(0, 0, shadowMap.getBuffer().getWidth(), shadowMap.getBuffer().getHeight());
		glClear(GL_DEPTH_BUFFER_BIT);
		int lastShader = 0;
		for (int i = 0; i < dynamicRenderStack.size(); i++) {
			if (lastShader != dynamicRenderStack.get(i).getDepthShader()) {
				glUseProgram(dynamicRenderStack.get(i).getDepthShader());
			}
			dynamicRenderStack.get(i).render();
		}
		glViewport(0, 0, this.getWidth(), this.getHeight());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(0);
		glCullFace(GL_BACK);
	}
	

	void renderShadowMap(ShadowMap map, ArrayList<RenderObject> objects, int buffer) {
		map.updateCameraUniform();
		glCullFace(GL_FRONT);
		//glBindFramebuffer(GL_FRAMEBUFFER, map.getBuffer().getColorMapFBO());
		glBindFramebuffer(GL_FRAMEBUFFER, buffer);
		glViewport(0, 0, map.getBuffer().getWidth(), map.getBuffer().getHeight());
		glClear(GL_DEPTH_BUFFER_BIT);
		int lastShader = 0;
		for (int i = 0; i < objects.size(); i++) {
			if (lastShader != objects.get(i).getDepthShader()) {
				glUseProgram(objects.get(i).getDepthShader());
			}
			objects.get(i).render();
		}
		glViewport(0, 0, this.getWidth(), this.getHeight());
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(0);
		glCullFace(GL_BACK);
		
	}
	
	/**
	 * Render several lists with {@link RenderObject}.
	 * @param dynamicShadowMap
	 * @param arrayLists
	 */
	@SafeVarargs
	final void renderTextured(ShadowMap dynamicShadowMap, ArrayList<? extends RenderObject>... arrayLists ) {
		dynamicShadowMap.updateCameraUniform();

		int lastShader = 0;
		for (ArrayList<? extends RenderObject> renderlist : arrayLists) {
			for (RenderObject o : renderlist) {
				if (lastShader != o.getShader()) {
					lastShader = o.getShader();
					glUseProgram(lastShader);
				}
				o.renderTextured();
			}
		}
		glUseProgram(0);
	}
	
	
	@Deprecated
	void renderTextured(ShadowMap dynamicShadowMap) {
		dynamicShadowMap.updateCameraUniform();
		
		int lastShader = 0;		
		for (int i = 0; i < staticRenderStack.size(); i++) {
			if (lastShader != staticRenderStack.get(i).getShader()) {
				lastShader = staticRenderStack.get(i).getShader();
				glUseProgram(lastShader);
			
			}
			staticRenderStack.get(i).renderTextured();
		}
		
		for (int i = 0; i < dynamicRenderStack.size(); i++) {
			if (lastShader != dynamicRenderStack.get(i).getShader()) {
				lastShader = dynamicRenderStack.get(i).getShader();
				glUseProgram(lastShader);
			}
			dynamicRenderStack.get(i).renderTextured();
		}
		glUseProgram(0);
	}
	
	/**
	 * Bind the sky color to a color. If the color values of the argument change the sky color will also change.
	 * @param color - The color to bind. The sky color will automatically update to this color.
	 */
	public void bindSkyColor(Color color) {
		this.skyColor = color;
	}
	
	public void setSkyColor(Vector4f v) {
		this.skyColor.setColor(v);
	}
	
	public void setSkyColor(Color c) {
		this.skyColor.setColor(c);
	}
	
	public Vector4f getSkyColor() {
		return this.skyColor.getColor();
	}
	
	@Deprecated
	void addStaticRenderObject(RenderObject obj) {
		this.staticRenderStack.add(obj);
	}
	
	@Deprecated
	void addDynamicRenderObject(RenderObject obj) {
		this.dynamicRenderStack.add(obj);
	}
	
	void deleteRenderObject(RenderObject obj) {
		if(staticRenderStack.contains(obj)) {
			staticRenderStack.remove(obj);
		} else if(dynamicRenderStack.contains(obj)) {
			dynamicRenderStack.remove(obj);
		}
		
	}
	
	void deleteAllRenderObjects() {
		for(int i = 0; i < staticRenderStack.size(); i++) {
			staticRenderStack.get(i).discard();
		}
		
		for(int i = 0; i < dynamicRenderStack.size(); i++) {
			dynamicRenderStack.get(i).discard();
		}
	}
	
	void enableVertexArray() {
		glEnableClientState(GL_VERTEX_ARRAY);
	}
	
	void enableNormals() {
		glEnableClientState(GL_NORMAL_ARRAY);
	}
	
	void enableTexture() {
		glEnable(GL_TEXTURE_2D);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}