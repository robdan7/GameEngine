package core.graphics.renderUtils;

import core.graphics.renderUtils.uniforms.UniformBufferSource;
import core.graphics.shading.GLSLvariableType;
import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

/**
 * 
 * @author Robin
 *
 */
public class Camera {
	private Matrix4f lookAtMatrix;
	private Matrix4f perspectiveMatrix;
	private Vector3f focusPosition;
	private boolean focusIsBound = false;
	private final Vector3f cameraPosition;
	private Vector3f forward;
	private Vector3f up, right;
	private  float hAngle;
	private float vAngle;
	private float focusoffset;

	private updateType updateType;
	
	private UniformBufferSource uniformSource;
	
	/**
	 * 
	 * @param fovy - Field of view.
	 * @param aspect - window with / window height.
	 * @param zNear - near plane. Not zero.
	 * @param zFar - far plane.
	 * @param name - uniform shader name.
	 */
	public Camera(Vector3f upVector, Vector3f right, float fovy, float aspect, float zNear, float zFar, updateType update, UniformBufferSource uniform) {
		this.uniformSource = uniform;
		//super(update.getSize());
		//super(glVariableType.MATRIX4F, name1, name2);
		cameraPosition = new Vector3f();
		init(upVector, right, update);
		perspectiveMatrix.setPerspective(fovy, aspect, zNear, zFar);
	}
	
	public Camera(Vector3f up, Vector3f rightV, float left, float right, float bottom, float top, float zNear, float zFar, updateType update, UniformBufferSource uniform) {
		this.uniformSource = uniform;
		//super(update.getSize());
		//super(glVariableType.MATRIX4F, name1, name2);
		cameraPosition = new Vector3f();
		init(up, rightV, update);
		perspectiveMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}
	
	/*
	@Deprecated
	public Camera(Vector3f up, Vector3f rightV, float left, float right, float bottom, float top, float zNear, float zFar, updateType update, String name1) {
		//super(update.getSize());
		this.uniformSource = new UniformBufferSource(GLSLvariableType.MAT4, name1);
		cameraPosition = new Vector3f();
		init(up, rightV, update);
		perspectiveMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}*/
	
	/**
	 * Init matrices for camera.
	 */
	private void init(Vector3f upVector, Vector3f right, updateType update) {
		this.updateType = update;
		this.focusPosition = new Vector3f();
		this.up = upVector;
		this.right = right;
		lookAtMatrix = new Matrix4f();
		perspectiveMatrix = new Matrix4f();
		
		forward = upVector.crossProduct(right);
		forward.normalize();
	}
	
	public void perspective(float fovy, float aspect, float zNear, float zFar, String name) {
		perspectiveMatrix.setPerspective(fovy, aspect, zNear, zFar);
	}
	
	/**
	 * Add (delta) rotation to the camera.
	 * @param horizontalAngle - Horizontal delta angle.
	 * @param verticalAngle - Vertical delta angle.
	 */
	public void rotate (float horizontalAngle, float verticalAngle) {
		this.hAngle += horizontalAngle;
		this.vAngle += verticalAngle;
		
		this.hAngle %= Math.PI*2;
		this.vAngle %= Math.PI*2;
		
		this.updateForward();
	}
	
	/**
	 * 
	 * @param horizontalAngle
	 * @param verticalAngle
	 */
	public void rotateAbsolute (float horizontalAngle, float verticalAngle) {
		this.hAngle = horizontalAngle;
		this.vAngle = verticalAngle;
		
		this.hAngle %= Math.PI*2;
		this.vAngle %= Math.PI*2;
		
		this.updateForward();
	}
	
	private void updateForward() {
		forward.setZ((float)(Math.cos(hAngle)*Math.cos(vAngle)));
		forward.setX((float)(Math.sin(hAngle)*Math.cos(vAngle)));
		forward.setY((float)Math.sin(vAngle));
		this.forward.normalize();
	}
	
	/**
	 * Rotate the camera matrix around a point and move it.
	 */
	public void lookAt() {
		this.updateCamPos();
		this.lookAtMatrix.setIdentity();
		
		this.lookAtMatrix.lookAt(this.cameraPosition, this.forward, this.up, this.right);
		//this.perspectiveMatrix.multiply(this.lookAtMatrix).put(this.uniformBuffer);
	}
	
	/**
	 * Look at a vector relative to camera position.
	 * @param v - The vector v to look at.
	 */
	public void lookAt(Vector3f v) {
		this.forward.set(v);
		this.lookAt();
	}	
	
	/**
	 * Look at a vector relative to camera position.
	 * @param v - The vector v to look at.
	 */
	public void lookAt(Vector4f v) {
		this.forward.set(v);
		this.lookAt();
	}	
	
	/**
	 * Updates the uniform block connected to this camera.
	 */
	public void updateUniform() {
		switch(this.updateType) {
			case CAMERA:
				this.uniformSource.updateSource(this.perspectiveMatrix.multiply(this.lookAtMatrix));
				break;
			case VIEW:
				this.uniformSource.updateSource(this.lookAtMatrix);
				break;
			case BOTH:
				this.uniformSource.updateSource(0, this.perspectiveMatrix.multiply(this.lookAtMatrix), this.lookAtMatrix);
				break;
		}	
	}
	
	/**
	 * Updates the camera position in regards to the focus position and offset to it.
	 */
	private void updateCamPos() {
		this.cameraPosition.set(this.getFocusPos());
		if (this.focusoffset > 0) {			
			this.cameraPosition.subtract(this.forward.asMultiplied(this.focusoffset));
		}
	}
	
	/**
	 * Set the focus position of the camera. The position cannot be changed if it is bound to another object.
	 * @param v - the new camera position;
	 */
	public void setFocusPos(Vector3f v) {
		if(this.focusIsBound) {
			throw new RuntimeException("Position is bound to another object");
		}
		this.getFocusPos().set(v);
	}
	
	public void bindFocusPos(Vector3f v) {
		this.focusIsBound = true;
		this.focusPosition = v;
	}
	
	public void unbindFocus() {
		this.focusIsBound = false;
		this.focusPosition = this.focusPosition.toVec3f(); // Make a copy.
	}
	
	/**
	 * Set the offset to the focus position. A positive offset moves the camera back.
	 * @param offset
	 */
	public void setFocusOffset(float offset) {
		this.focusoffset = offset;
	}
	
	/**
	 * Set the position to a chosen vector. The two objects will now point to the same data.
	 * The position cannot be changed if it is already bound to another object.
	 * @param v
	 */
	public void copyFocusPos(Vector3f v) {
		if (focusIsBound) {
			throw new RuntimeException("Position is bound to another object");
		}
		this.focusPosition.set(v);
	}
	
	public Vector3f getCamPos() {
		return this.cameraPosition;
	}
	
	public Vector3f getFocusPos() {
		return this.focusPosition;
	}
	
	public Vector3f getForward() {
		return this.forward;
	}
	
	public Matrix4f getLookAtMatrix() {
		return this.lookAtMatrix;
	}
	
	public Matrix4f getPerspectiveMatrix() {
		return this.perspectiveMatrix;
	}
	
	public float getCamOffset() {
		return this.focusoffset;
	}
	
	/**
	 * 
	 * @return - The horizontal angle.
	 */
	public float gethAngle() {
		return this.hAngle;
	}
	/**
	 * 
	 * @return - The vertical angle.
	 */
	public float getvAngle() {
		return this.vAngle;
	}
	
	/**
	 * Work in progress. Defines the size of all connected view matrices.
	 * CAMERA: one matrix.
	 * VIEW: one matrix.
	 * BOTH: one camera matrix and one view matrix.
	 * @author Robin
	 *
	 */
	public static enum updateType {
		CAMERA(Matrix4f.SIZE),VIEW(Matrix4f.SIZE),BOTH(Matrix4f.SIZE*2);
		private int size;
		private updateType(int size) {
			this.size = size;
		}
		
		public int getSize() {
			return this.size;
		}
	}
}
