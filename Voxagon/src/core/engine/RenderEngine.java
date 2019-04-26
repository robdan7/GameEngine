package core.engine;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import core.graphics.renderUtils.Framebuffer;
import core.graphics.renderUtils.RenderObject;
import core.utils.datatypes.GlueList;
import core.utils.datatypes.Stack;


public class RenderEngine {
	private Stack<RenderContext> rendercontexts;
	private long window;

	public RenderEngine(long window) {
		this.rendercontexts = new Stack<RenderContext>();
		this.rendercontexts.push(new RenderContext());
		this.window = window;
	}
	
	/**
	 * Render all stages in the current render context.
	 */
	public void renderAll() {
		this.rendercontexts.getTop().renderAll();
		glfwSwapBuffers(this.window);	// Swap the buffers so everything is shown in the window.
	}
	
	public void setRenderContext(RenderContext context) {
		this.rendercontexts.push(context);
	}
	
	/**
	 * Remove the active render context from the rendering stack.
	 * @return The active render context.
	 */
	public RenderContext popActiveRendercontext() {
		return this.rendercontexts.pull();
	}
	
	public void popRenderContext(RenderContext context) {
		if (!this.rendercontexts.extract(context)) {
			throw new IllegalArgumentException("the context does not exist! Nothing was removed.");
		}
	}
	
	/**
	 * Disable a render stage. A disabled render stage won't output 
	 * anything to it's frame buffer.
	 * @param stage
	 */
	public void disableRenderStage(RenderStage stage) {
		if (!this.rendercontexts.getTop().getRendeStages().contains(stage)) {
			throw new IllegalArgumentException("The render stage does not exist in this render engine.");
		}
		stage.disableStage();
	}
	
	/**
	 * Enable a render stage. All stages are enabled by default.
	 * @param stage
	 */
	public void enablerenderStage(RenderStage stage) {
		if (!this.rendercontexts.getTop().getRendeStages().contains(stage)) {
			throw new IllegalArgumentException("The render stage does not exist in this render engine.");
		}
		stage.enableStage();
	}
	
	/**
	 * Create a new render stage and add the stage to the end of the pipeline. The frame buffer is 
	 * automatically cleared before rendering.
	 * @param framebuffer
	 * @param objects
	 * @return The newly created render stage.
	 */
	public RenderStage addRenderStage(Framebuffer framebuffer, GlueList<RenderObject> objects) {
		RenderStage renderStage = new RenderStage(framebuffer, objects);
		this.rendercontexts.getTop().getRendeStages().add(renderStage);
		return renderStage;
	}
	
	/**
	 * Create a new render stage and add the stage to the end of the pipeline.
	 * @param framebuffer - The frame buffer.
	 * @param objects - The objects to render.
	 * @param clearOnRender - Set this to true if the buffer should be cleared before rendering.
	 * @return - The newly created render stage.
	 */
	public RenderStage addRenderStage(Framebuffer framebuffer, GlueList<RenderObject> objects, boolean clearOnRender) {
		RenderStage stage = this.addRenderStage(framebuffer, objects);
		stage.setClearOnRender(clearOnRender);
		return stage;
	}
}
