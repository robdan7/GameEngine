package core.utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * 3-dimensional vector.
 * @author Robin
 *
 */
public class Vector3f extends Vector<Vector3f> {
	public static final int SIZE = 3;

	public Vector3f() {
		super();
	}

	/**
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @param z
	 *            Z coordinate.
	 */
	public Vector3f(float x, float y, float z) {
		super(x,y,z);
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getZ() {
		return this.z;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void set(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	@Override
	public void set(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
	}

	@Override
	public void set(Vector4f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}


	@Override
	public float[] asFloats() {
		return new float[] { this.x, this.y, this.z };
	}


	@Override
	public String toString() {
		return this.x + " , " + this.y + " , " + this.z;
	}

	@Override
	public Vector3f copy() {
		// TODO Auto-generated method stub
		return new Vector3f(this.x, this.y, this.z);
	}

	@Override
	public Vector3f create() {
		return new Vector3f();
	}

	public FloatBuffer asFloatBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		return buffer.put(this.asFloats());
	}
}
