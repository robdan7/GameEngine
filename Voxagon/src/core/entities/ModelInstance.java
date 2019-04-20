package core.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;

public class ModelInstance {
	private Vector3f position;
	private Model parent;		/* Model parent */
	private Matrix4f matrix;	/* Translation matrix for this instance */
	private FloatBuffer instanceBuffer;
	
	ModelInstance(Model parent, Vector3f position) {
		this.position = position;
		this.matrix = new Matrix4f();
		this.parent = parent;
		//this.instanceBuffer = BufferUtils.createFloatBuffer(16); // one matrix.
		//this.instanceBuffer.clear();
		//this.matrix.put(this.instanceBuffer);
		this.instanceBuffer = BufferUtils.createFloatBuffer(4);
		
		this.instanceBuffer.put(this.position.asFloats());
	}
	
	ModelInstance(Model parent) {
		this(parent, new Vector3f());
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	/**
	 * Return the transformation matrix for this model instance.
	 * @return
	 */
	Matrix4f getMatrix() {
		return this.matrix;
	}
	
	FloatBuffer getInstanceData() {
		return this.instanceBuffer;
	}
	
	public Model getParent() {
		return this.parent;
	}
	
	public void setMatrixIdentity() {
		this.matrix.setIdentity();
	}
	
	/**
	 * Rotate this mesh around the current center. Rotation 
	 * before translation yields a different result.
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotateMesh(double angle, float x, float y, float z) {
		this.matrix.rotateAbsolute(angle, x, y, z);
	}
	
	/**
	 * Rotate this mesh around the current center. Rotation 
	 * before translation yields a different result.
	 * @param angle
	 * @param xyz
	 */
	public void rotateMesh(double angle, Vector3f xyz) {
		this.rotateMesh(angle, xyz.getX(), xyz.getY(), xyz.getZ());
	}
	
	public void moveMesh(float x, float y, float z) {
		this.matrix.translate(x,y,z);
	}
	
	public void moveMesh(Vector3f v) {
		this.matrix.translate(v);
	}
	
	/**
	 * Set the absolute position of this instance. The position is relative 
	 * to the parent position (the model itself).
	 * @param position
	 */
	public void setAxisPosition(Vector3f position) {
		this.position.set(position);
		this.matrix.absoluteAxisTranslation(position);
	}
	
	public void setRotatedPosition(Vector3f position) {
		this.matrix.translateAbsolute(position);
		this.position.setZero();
		this.matrix.multiply(this.position);
	}
}
