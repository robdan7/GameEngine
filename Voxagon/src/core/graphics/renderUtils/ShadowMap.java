package core.graphics.renderUtils;

import org.lwjgl.opengl.GL11;

import core.graphics.lights.DirectionalLight;
import core.graphics.lights.Light;
import core.graphics.misc.Texture;
import core.graphics.renderUtils.uniforms.UniformBufferSource;
import core.physics.mechanics.BufferedMotionContainer;
import core.utils.math.MathTools;
import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

public class ShadowMap {
	
	/**
	 * The camera takes care of positioning the shadow map.
	 */
	private Camera cam;
	
	/**
	 * This stores the finished rendering of the scene.
	 */
	//private Drawbuffer textureBuffer;
	private Framebuffer framebuffer;

	/**
	 * Every shadow map need a light source. It can only be directional atm.
	 */
	private DirectionalLight light;
	
	//private Vector3f position;
	
	private BufferedMotionContainer positionContainer;
	
	private float xPPI, yPPI;
	
	/**
	 * @param up - up vector.
	 * @param right - right Vector. Must not be the same as up.
	 * @param shader - The shader to bind it to.
	 * @param textureName - The shader texture name.
	 * @param width - pixel width.
	 * @param height - pixel height.
	 * @param imageFilter - Image filer. GL_LINEAR or GL_NEAREST.
	 * @param matrixDimensions - The orthographic camera dimensions (width, height, near, far)
	 * @throws Exception
	 */
	public ShadowMap(Vector3f up, Vector3f right, String textureName, int width, int height, int imageFilter, Vector4f matrixDimensions, UniformBufferSource unf) throws Exception {

		this.framebuffer = new Framebuffer(width, height);
		this.framebuffer.addDepthAttachment(textureName, imageFilter, GL11.GL_DEPTH_COMPONENT);

		cam = new Camera(
				up, right, -matrixDimensions.getX(), matrixDimensions.getX(), -matrixDimensions.getY(), matrixDimensions.getY(), matrixDimensions.getZ(), matrixDimensions.getW(), 
				Camera.updateType.CAMERA, unf);

		Vector3f v = new Vector3f(0.1f,-1f,0);
		v.normalize();
		this.lookAt(v);
		cam.lookAt();

		this.positionContainer = new BufferedMotionContainer();
		
		this.xPPI = matrixDimensions.getX()*2.0f/(float)width;
		this.yPPI = matrixDimensions.getY()*2.0f/(float)height;
	}
	
	/**
	 * 
	 * @param v - A normalized vector.
	 */
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
		//this.cam.bindFocusPos(v);
		//this.position = v;
		this.positionContainer = new BufferedMotionContainer(v);
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
		roundOffAndUpdateCamPos();
		//this.cam.getFocusPos().set(this.position);
		if (this.light != null) {
			/**
			 * Use the flipped light position to look in the opposite direction.
			 * The camera will now look in the same direction as the light source.
			 */
			cam.lookAt(light.getPosition().asFlipped());
		}
		cam.updateUniform();
	}
	
	
	/**
	 * This rounds off the shadow map position before it updates 
	 * the camera position. This ensures that all shadow edges look the same 
	 * on non-moving objects in the scene.
	 */
	private void roundOffAndUpdateCamPos() {
		this.positionContainer.resetBuffer();
		this.getCamera().getLookAtMatrix().multiply(this.positionContainer.getBufferedPosition());
		
		this.positionContainer.storeBufferedPositionX(MathTools.floorToMultiple(this.positionContainer.getBufferedPosition().getX(), this.xPPI));
		this.positionContainer.storeBufferedPositionY(MathTools.floorToMultiple(this.positionContainer.getBufferedPosition().getY(), this.yPPI));
		
		Matrix4f inverse = this.getCamera().getLookAtMatrix().getInverse();
		
		inverse.multiply(this.positionContainer.getBufferedPosition());

		this.cam.getFocusPos().set(this.positionContainer.getBufferedPosition());
	}
	
	/**
	 * 
	 * @return - The draw buffer used for rendering to the texture.
	 */
	public int getBufferIndex() {
		//return this.textureBuffer;
		return this.framebuffer.getFramebuffer();
	}
	
	public int getHeight() {
		return this.framebuffer.getHeight();
	}
	
	public int getWidth() {
		return this.framebuffer.getWidth();
	}
	
	/**
	 * 
	 * @return - The generated shadow map depth texture.
	 */
	public Texture getTexture() {
		//return this.textureBuffer.getDepthMapTexture();
		return this.framebuffer.getDepthAttachment();
	}
	
	public Light getLightSource() {
		return this.light;
	}
}
