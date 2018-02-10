package objects;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import utils.math.Matrix4f;
import utils.math.Vector;
import utils.math.Vector3f;
import utils.rendering.Shaders;

/**
 * 
 * @author Robin
 *
 */
public class Camera{
	private Matrix4f lookAtMatrix;
	private Matrix4f perspectiveMatrix;
	private String name;
	private Vector3f position;
	private Vector3f forward;
	private Vector3f up;
	private float hAngle;
	float vAngle;

	/**
	 * 
	 * @param fovy - Field of view.
	 * @param aspect - window with / window height.
	 * @param zNear - near plane. Not zero.
	 * @param zFar - far plane.
	 * @param name - uniform shader name.
	 */
	public Camera(Vector3f upVector, float fovy, float aspect, float zNear, float zFar, String name) {
		this.name = name;
		init(upVector);
		perspectiveMatrix.setPerspective(fovy, aspect, zNear, zFar);
	}
	
	public Camera(float left, float right, float bottom, float top, float zNear, float zFar, String name) {
		this.name = name;
		init(new Vector3f(0,1,0));
		perspectiveMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}
	
	/**
	 * Init matrices for camera.
	 */
	private void init(Vector3f upVector) {
		this.up = upVector;
		lookAtMatrix = new Matrix4f();
		perspectiveMatrix = new Matrix4f();
		forward = new Vector3f(0,0,1);
		position = new Vector3f(0,0,0);
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
	 *//*
	public void rotateAbsolute (float horizontalAngle, float verticalAngle) {
		this.hAngle = horizontalAngle;
		this.vAngle = verticalAngle;
		forward.x = (float)(Math.cos(hAngle)*Math.sin(vAngle));
		forward.z = (float)Math.cos(hAngle);
		forward.y = (float)Math.sin(vAngle);
		
		this.forward.normalize();
	}*/
	
	/**
	 * Set the absolute position for the camera.
	 * @param v - the new camera position;
	 */
	public void setPosition(Vector3f v) {
		this.position.x = v.x;
		this.position.y = v.y;
		this.position.z = v.z;
	}
	
	/**
	 * Set the position to a choosen vector. The two objects will now point to the same data.
	 * @param v
	 */
	public void copyPosition(Vector3f v) {
		this.position = v;
	}

	
	/**
	 * Rotate the camera matrix around a point and move it.
	 */
	public void lookAt() {
		this.lookAtMatrix.setIdentity();
		//
		this.lookAtMatrix.lookAt(this.position, this.forward, this.up);
	}
	
	/**
	 * Rotate the camera matrix around a point and move it.
	 */
	public void lookAt(float offset) {
		this.lookAtMatrix.setIdentity();
		//
		this.lookAtMatrix.lookAt(this.position.subtract(this.forward.multiply(offset)), this.forward, this.up);
	}
	
	/**
	 * Look at a vector relative to camera position.
	 * @param v - The vector v to look at.
	 */
	public void lookAt(Vector v) {
		this.forward = (Vector3f) v.normalize().toVec3f();
		this.lookAt();
	}
	
	/**
	 * Update camera view for rendering.
	 * @param shader - The shader it should be bound to
	 */
	public void updateCamera(int shader) {
		GL20.glUseProgram(shader);
		perspectiveMatrix.createUniform(shader, perspectiveMatrix.multiply(lookAtMatrix).put(), this.name);
	}
	
	public void updateCamera(int shader, String name) {
		GL20.glUseProgram(shader);
		perspectiveMatrix.createUniform(shader, perspectiveMatrix.multiply(lookAtMatrix).put(), name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	public Vector3f getForward() {
		return this.forward;
	}
	
	public Matrix4f getLookAtMatrix() {
		return this.lookAtMatrix;
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
}
