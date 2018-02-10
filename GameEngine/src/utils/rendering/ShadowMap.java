package utils.rendering;

import objects.Camera;
import objects.lights.DirectionalLight;
import objects.lights.Light;
import utils.math.Vector3f;
import utils.math.Vector4f;
import utils.other.Texture;

import static org.lwjgl.opengl.GL11.*;

public class ShadowMap {
	private Camera cam;
	private Drawbuffer buffer;
	private DirectionalLight light;
	private static String sharedMatrixName;
	/**
	 * 
	 * @param shader - The shader to bind it to.
	 * @param textureName - The shader texture name.
	 * @param matrixName - The shader matrix name.
	 * @param width - pixel width.
	 * @param height - pixel height.
	 * @param imageFilter - Image filer. GL_LINEAR or GL_NEAREST.
	 * @param matrixDimensions - The orthographic camera dimensions.
	 * @throws Exception
	 */
	public ShadowMap(Shaders shader, String textureName, String matrixName, int width, int height, int imageFilter, Vector4f matrixDimensions) throws Exception {
		buffer = new Drawbuffer(shader, textureName, width, height, imageFilter, GL_DEPTH_COMPONENT);
		//buffer.getColorMapTexture().bindAsUniform(shader.getShaderProgram());
		cam = new Camera(-matrixDimensions.x, matrixDimensions.x, -matrixDimensions.y, matrixDimensions.y, matrixDimensions.z, matrixDimensions.w, matrixName);
		//cam.rotate(0, -(float)Math.PI/2);
		cam.lookAt(new Vector3f(0.1f,-1f,0));
		//System.out.println(cam.getForward().toString() + " : " + cam.getPosition().toString());
		cam.lookAt();
	}
	
	public void lookAt(Vector3f v) {
		cam.lookAt(v);
	}
	
	/**
	 * Bind the shadow map to a directional light.
	 * @param light - The directional light to bind.
	 */
	public void bindToLight(DirectionalLight light) {
		this.light = light;
	}
	
	public void setLightPosition(Vector4f v) {
		if (this.light != null) {
			this.light.setPosition(v);
		} else {
			System.err.println("Shadow map is not bound to a light source.");
		}
	}
	
	/**
	 * Bind the shadow map position to a vector. They will both point to the same memory.
	 * @param v - The vector to bind as position.
	 */
	public void bindPositionTo(Vector3f v) {
		this.cam.copyPosition(v);
	}
	
	/**
	 * 
	 * @return The camera.
	 */
	public Camera getCamera() {
		return cam;
	}
	
	/**
	 * Update the shadow map camera view for rendering to the shadow map.
	 * @param shader - The shader to bind.
	 */
	public void updateCameraUniform(int shader) {
		if (this.light != null) {
			cam.lookAt(this.light.getPosition().flip());
			//System.out.println(this.light.getPosition().toString());
		}
		cam.updateCamera(shader);
	}
	
	/**
	 * Update the shadow map camera view for normal rendering.
	 * @param shader - The shader to bind.
	 * @param name - The name in the shader to use.
	 */
	public void updateRenderCameraUniform(int shader, String name) {
		cam.updateCamera(shader, name);
	}
	
	/**
	 * 
	 * @return - The draw buffer used for rendering to the texture.
	 */
	public Drawbuffer getBuffer() {
		return this.buffer;
	}
	
	/**
	 * 
	 * @return - The generated shadow map depth texture.
	 */
	public Texture getTexture() {
		return this.buffer.getColorMapTexture();
	}
	
	public static void setSharedMatrixName(String name) {
		sharedMatrixName = name;
	}
	
	public static String getSharedMatrixName() {
		return sharedMatrixName;
	}
	
	public Light getLightSource() {
		return this.light;
	}
}
