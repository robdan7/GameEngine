package core.graphics.renderUtils;

import static org.lwjgl.opengl.GL11.*;

import core.graphics.lights.DirectionalLight;
import core.graphics.lights.Light;
import core.graphics.renderUtils.buffers.Drawbuffer;
import core.utils.math.Vector;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;
import core.utils.other.Texture;

public class ShadowMap {
	private Camera cam;
	private Drawbuffer buffer;
	private DirectionalLight light;
	private static String sharedMatrixName;
	/**
	 * @param up - up vector.
	 * @param right - right Vector. Must not be the same as up.
	 * @param shader - The shader to bind it to.
	 * @param textureName - The shader texture name.
	 * @param width - pixel width.
	 * @param height - pixel height.
	 * @param imageFilter - Image filer. GL_LINEAR or GL_NEAREST.
	 * @param matrixDimensions - The orthographic camera dimensions.
	 * @throws Exception
	 */
	public ShadowMap(Vector3f up, Vector3f right, String textureName, int width, int height, int imageFilter, Vector4f matrixDimensions) throws Exception {
		buffer = new Drawbuffer(textureName, width, height);
		//buffer.getColorMapTexture().bindAsUniform(shader.getShaderProgram());
		cam = new Camera(up, right, -matrixDimensions.x, matrixDimensions.x, -matrixDimensions.y, matrixDimensions.y, matrixDimensions.z, matrixDimensions.w, Camera.updateType.CAMERA);
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
		this.cam.bindFocusPos(v);
	}
	
	/**
	 * 
	 * @return The camera.
	 */
	public Camera getCamera() {
		return cam;
	}
	
	/**
	 * Update the camera view.
	 */
	public void updateCameraUniform() {
		if (this.light != null) {
			cam.lookAt(Vector.flip(light.getPosition()).toVec3f()); // Use the fliped position to look in the opposite direction.
		}
		cam.updateUniform();
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
		return this.buffer.getDepthMapTexture();
	}
	
	public Light getLightSource() {
		return this.light;
	}
}
