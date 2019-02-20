package core.graphics.renderUtils;

import core.utils.math.Matrix4f;

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
	 * Set the shader to this object.
	 * @param shader
	 */
	public void setShader(Shaders shader);
	
	/**
	 * Set a depth shader.
	 * @param shader
	 */
	public void setDepthShader(Shaders shader);
	
	/**
	 * Get the shader.
	 * @return
	 */
	public int getShader();
	
	/**
	 * Get the depth shader.
	 * @return
	 */
	public int getDepthShader();
}

