package core.graphics.renderUtils;

import core.graphics.lights.DirectionalLight;
import core.graphics.lights.Light;
import core.graphics.renderUtils.buffers.Drawbuffer;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;
import core.utils.other.Texture;

public class ShadowMap {
	
	/**
	 * The camera takes care of positioning the shadow map.
	 */
	private Camera cam;
	
	/**
	 * This stores the finished rendering of the scene.
	 */
	private Drawbuffer textureBuffer;
	
	/**
	 * Every shadow map need a light source. It can only be directional atm.
	 */
	private DirectionalLight light;
	
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
		textureBuffer = new Drawbuffer(textureName, width, height);
		//buffer.getColorMapTexture().bindAsUniform(shader.getShaderProgram());
		cam = new Camera(up, right, -matrixDimensions.getX(), matrixDimensions.getX(), -matrixDimensions.getY(), matrixDimensions.getY(), matrixDimensions.getZ(), matrixDimensions.getW(), Camera.updateType.CAMERA);
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
	 * Bind the shadow map position to a vector. 
	 * N.B: Both vectors will be completely equal.
	 * @param v - The vector to bind.
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
	 * Update the camera view. This must be done before the shadow map position can change.
	 */
	public void updateCameraUniform() {
		if (this.light != null) {
			/**
			 * Use the flipped light position to look in the opposite direction.
			 * The camera will now look in the same direction as the light source.
			 */
			cam.lookAt(light.getPosition().asFlipped().toVec3f());
		}
		cam.updateUniform();
	}
	
	/**
	 * 
	 * @return - The draw buffer used for rendering to the texture.
	 */
	public Drawbuffer getBuffer() {
		return this.textureBuffer;
	}
	
	/**
	 * 
	 * @return - The generated shadow map depth texture.
	 */
	public Texture getTexture() {
		return this.textureBuffer.getDepthMapTexture();
	}
	
	public Light getLightSource() {
		return this.light;
	}
}
