package core.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

import core.graphics.misc.Color;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.ShadowMap;
import core.graphics.shading.Material;
import core.utils.datatypes.GlueList;
import core.utils.math.Vector4f;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Window {

	// The window handle
	private long window;
	private int width;
	private int height;
	
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
		
		GLFWWindowSizeCallback callback = new GLFWWindowSizeCallback() {

			@Override
			public void invoke(long arg0, int arg1, int arg2) {
				width = arg1;
				height = arg2;
			}
			
		};
		
		GLFW.glfwSetWindowSizeCallback(this.getWindow(), callback);
	}
	
	public void prepareToRender() {
		glClearColor(skyColor.getR(), skyColor.getG(), skyColor.getB(), skyColor.getAlpha());
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	void endRender() {
		glfwSwapBuffers(this.getWindow()); // swap the color buffers
	}


	void renderShadowMap(ShadowMap map, GlueList<RenderObject> objects) {
		map.updateCameraUniform();
		glCullFace(GL_FRONT);
		glBindFramebuffer(GL_FRAMEBUFFER, map.getBufferIndex());
		glViewport(0, 0, map.getWidth(), map.getHeight());
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
	final void renderTextured(GlueList<? extends RenderObject>... arrayLists ) {
		
		int lastShader = 0;
		
		for (GlueList<? extends RenderObject> renderlist : arrayLists) {
			for (RenderObject o : renderlist) {
				/*
				if (lastShader != o.getShader()) {
					lastShader = o.getShader();
					glUseProgram(lastShader);
				}
*/
				o.renderTextured();
			}
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
	

	
	void deleteRenderObject(GlueList<RenderObject> renderStack, RenderObject obj) {
		if(renderStack.contains(obj)) {
			renderStack.remove(obj);
		} else if(renderStack.contains(obj)) {
			renderStack.remove(obj);
		}
		
	}
	
	
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}