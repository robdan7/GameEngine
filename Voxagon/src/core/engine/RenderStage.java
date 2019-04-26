package core.engine;

import core.graphics.renderUtils.Framebuffer;
import core.graphics.renderUtils.RenderObject;
import core.utils.datatypes.GlueList;


/**
 * A rendering stage is a collection of objects and a frame buffer. 
 * When invoking {@link #render()}
 * @author Robin
 *
 */
public class RenderStage {
	private Framebuffer framebuffer;
	private GlueList<RenderObject> rendercontent;
	private boolean enabled = true, clearOnRender = true;
	
	RenderStage(Framebuffer framebuffer) {
		this.framebuffer = framebuffer;
		this.rendercontent = new GlueList<RenderObject>();
	}
	
	RenderStage(Framebuffer framebuffer, GlueList<RenderObject> renderObjects) {
		this.framebuffer = framebuffer;
		this.rendercontent = renderObjects;
	}
	
	RenderStage(Framebuffer framebuffer, GlueList<RenderObject> renderObject, boolean clearOnRender) {
		this(framebuffer, renderObject);
		this.clearOnRender = clearOnRender;
	}
	
	/**
	 * Add a render object to this stage.
	 * @param o
	 */
	public void addRenderObject(RenderObject o) {
		this.rendercontent.add(o);
	}
	
	/**
	 * Delete a render object in this render stage. It is not allowed to 
	 * remove an object that does not exist.
	 * @param o
	 */
	public void deleteRenderObject(RenderObject o) {
		if (!this.rendercontent.contains(o)) {
			throw new IllegalArgumentException("The object doe not exist in this render stage.");
		}
		this.rendercontent.remove(o);
	}
	
	
	/**
	 * Disable the render stage. A disabled render stage won't output anything to 
	 * it's frame buffer.
	 */
	void disableStage () {
		this.enabled = false;
	}
	
	/**
	 * Enable the render stage. All stages are enabled by default.
	 */
	void enableStage() {
		this.enabled = true;
	}
	
	/**
	 * Enable or disable clearing the frame buffer when rendering this stage. This enables 
	 * several stages to render to the same buffer without overriding the previous render.
	 * @param clear
	 */
	void setClearOnRender(boolean clear) {
		this.clearOnRender = clear;
	}
	
	/**
	 * Set the frame buffer to the active buffer and render all objects 
	 * currently active in the render stage.
	 */
	void render() {
		if (!this.enabled) {
			return;
		}
		this.framebuffer.bindBuffer();
		if (this.clearOnRender) this.framebuffer.clearBuffer();
		for (RenderObject obj : this.rendercontent) {
			//obj.render();
			obj.renderTextured();
		}
		//glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}