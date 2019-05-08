package core.graphics.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import core.utils.datatypes.buffers.FloatBufferPartition;
import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;

public abstract class ModelInstance {
	private Vector3f position;
	private Model parent;		/* Model parent */
	private Matrix4f matrix;	/* Translation matrix for this instance */
	private FloatBufferPartition bufferpartition;
	
	
	/**
	 * Create a model instance object.
	 * @param parent
	 * @param root
	 * @param instanceDataSize
	 */
	protected ModelInstance(Model parent, Element root, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		this.position = this.createPosition(root);
		this.matrix = new Matrix4f();
		this.parent = parent;
		
		this.bufferpartition = new FloatBufferPartition(instanceBuffer, bufferStart, bufferStop);
	}
	
	/**
	 * Create a model instance without specified root element.
	 * The default values will be assigned instead.
	 * @param parent
	 * @param instanceDataSize
	 */
	protected ModelInstance(Model parent, int instanceDataSize, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		this.position = new Vector3f();
		this.matrix = new Matrix4f();
		this.bufferpartition = new FloatBufferPartition(instanceBuffer, bufferStart, bufferStop);
	}
	
	private Vector3f createPosition(Element root) {
		NodeList nodes= root.getElementsByTagName("position");
		String[] vectorString = nodes.item(0).getTextContent().split("\\s+");
		int x = Integer.parseInt(vectorString[0]);
		int y = Integer.parseInt(vectorString[1]);
		int z = Integer.parseInt(vectorString[2]);
		
		return new Vector3f(x,y,z);
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
	
	protected FloatBufferPartition getInstanceData() {
		return this.bufferpartition;
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
