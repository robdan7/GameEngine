package core.graphics.renderUtils;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import core.graphics.renderUtils.uniforms.UniformSource;
import core.utils.math.Matrix4f;
import core.utils.math.Vector;
import core.utils.math.Vector3f;

/**
 * 
 * @author Robin
 *
 */
public class Camera extends UniformSource {
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
	
	private FloatBuffer uniformBuffer;
	private updateType update;
	
	/**
	 * 
	 * @param fovy - Field of view.
	 * @param aspect - window with / window height.
	 * @param zNear - near plane. Not zero.
	 * @param zFar - far plane.
	 * @param name - uniform shader name.
	 */
	public Camera(Vector3f upVector, Vector3f right, float fovy, float aspect, float zNear, float zFar, updateType update) {
		super(update.getSize());
		cameraPosition = new Vector3f();
		init(upVector, right, update);
		perspectiveMatrix.setPerspective(fovy, aspect, zNear, zFar);
	}
	
	public Camera(Vector3f up, Vector3f rightV, float left, float right, float bottom, float top, float zNear, float zFar, updateType update) {
		super(update.getSize());
		cameraPosition = new Vector3f();
		init(up, rightV, update);
		perspectiveMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}
	
	/**
	 * Init matrices for camera.
	 */
	private void init(Vector3f upVector, Vector3f right, updateType update) {
		this.update = update;
		this.focusPosition = new Vector3f();
		this.up = upVector;
		this.right = right;
		lookAtMatrix = new Matrix4f();
		perspectiveMatrix = new Matrix4f();
		forward = upVector.crossProduct(right).normalize().toVec3f();
		
		
		int uniformSize = Matrix4f.getSize();
		if (this.update.getSize() == Matrix4f.getSize()*2) {
			uniformSize = uniformSize *2;
		}
		this.uniformBuffer = BufferUtils.createFloatBuffer(uniformSize);
		
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
		
		forward.z = (float)(Math.cos(hAngle)*Math.cos(vAngle));
		forward.x = (float)(Math.sin(hAngle)*Math.cos(vAngle));
		forward.y = (float)Math.sin(vAngle);
		this.forward.normalize();
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
		
		forward.z = (float)(Math.cos(hAngle)*Math.cos(vAngle));
		forward.x = (float)(Math.sin(hAngle)*Math.cos(vAngle));
		forward.y = (float)Math.sin(vAngle);
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
		this.forward = v.normalize().toVec3f();
		this.lookAt();
	}	
	
	/**
	 * DO NOT USE THIS
	 */
	@Deprecated
	@Override
	public void updateUniform(float[] buffer) {
		if (buffer != null) {
			throw new RuntimeException("This method is not supported for this instance");
		}
	}
	
	public void updateUniform() {
		switch(this.update) {
			case CAMERA:
				this.perspectiveMatrix.multiply(this.lookAtMatrix).put(this.uniformBuffer);
				break;
			case VIEW:
				this.lookAtMatrix.put(this.uniformBuffer);
				break;
			case BOTH:
				this.perspectiveMatrix.multiply(this.lookAtMatrix).put(this.uniformBuffer);
				this.uniformBuffer.put(this.lookAtMatrix.toFloatArray());
				break;
		}
		
		//this.perspectiveMatrix.multiply(this.lookAtMatrix).put(this.uniformBuffer);
		super.updateUniform(this.uniformBuffer);
	}
	
	/**
	 * Updates the camera position in regards to the focus position and offset to it.
	 */
	private void updateCamPos() {
		if (this.focusoffset == 0) {
			Vector.copy(this.getFocusPos(), this.cameraPosition);
		} else {
			Vector offset = this.forward.copy().multiply(this.focusoffset);
			Vector.copy(Vector.subtract(this.getFocusPos(), offset),this.cameraPosition);
		}
	}
	
	/**
	 * Set the focus position of the camera. The position cannot be changed if it is bound to another object.
	 * @param v - the new camera position;
	 */
	public void setFocusPos(Vector v) {
		if(this.focusIsBound) {
			throw new RuntimeException("Position is bound to another object");
		}
		Vector.copy(v, this.getFocusPos());
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
	 * Set the position to a choosen vector. The two objects will now point to the same data.
	 * The position cannot be changed if it is bound to another object.
	 * @param v
	 */
	public void copyFocusPos(Vector3f v) {
		if (focusIsBound) {
			throw new RuntimeException("Position is bound to another object");
		}
		this.focusPosition.x = v.x;
		this.focusPosition.y = v.y;
		this.focusPosition.z = v.z;
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
	
	public static enum updateType {
		CAMERA(Matrix4f.getSize()),VIEW(Matrix4f.getSize()),BOTH(Matrix4f.getSize()*2);
		private int size;
		private updateType(int size) {
			this.size = size;
		}
		
		public int getSize() {
			return this.size;
		}
	}
}
