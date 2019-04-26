package core.graphics.renderUtils;

/**
 * This class represents anything that can be displayed on the monitor.
 * @author Robin
 *
 */
public interface RenderObject {

	/**
	 * Plain render method.
	 */
	public void render();
	
	/**
	 * Render with textures on.
	 */
	public void renderTextured();
	
	/**
	 * Remove this object from the OpenGL pipeline.
	 */
	public void discard();
	

	
	/**
	 * Set a depth shader.
	 * @param shader
	 */
	public void setDepthShader(Shaders shader);
}

