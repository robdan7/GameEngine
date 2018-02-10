package utils.math;

/**
 * 
 * @author Robin
 *
 */
public class Vector3f implements Vector {
	public float x;
	public float y;
	public float z;

	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * 
	 * @param x
	 *            - X coordinate.
	 * @param y
	 *            - Y coordinate.
	 * @param z
	 *            - Z coordinate.
	 */
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	@Override
	public Vector normalize() {
		float length = this.length();
		if (length != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
			this.z = this.z / length;
		}
		return this;
	}

	@Override
	public Vector add(Vector v) {
		this.x += v.toVec3f().x;
		this.y += v.toVec3f().y;
		this.z += v.toVec3f().z;
		return this;
	}

	/**
	 * Subtracts a vector by another vector.
	 * 
	 * @param v
	 *            - The vector to subtract with.
	 * @return The new subtracted vector.
	 */
	public Vector3f subtract(Vector3f v) {
		return new Vector3f(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	public Vector3f multiply(float m) {
		return new Vector3f(this.x * m, this.y * m, this.z * m);
	}

	public Vector3f copy() {
		return new Vector3f(this.x, this.y, this.z);
	}

	@Override
	public float length() {
		return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}

	/**
	 * 
	 * @param v
	 * @return The cross product between two vectors.
	 */
	public Vector3f crossProduct(Vector3f v) {
		Vector3f v2 = new Vector3f();
		v2.x = this.y * v.z - v.y * this.z;
		v2.y = this.z * v.x - v.z * this.x;
		v2.z = this.x * v.y - v.x * this.y;
		if (this.length() == 0) {
			System.err.println("Vector cross product is zero.");
		}
		return v2;
	}

	public Vector3f interpolate(Vector3f v, float t) {
		Vector3f v2 = new Vector3f(0, 0, 0);
		v2.x = this.x + t * (v.x - this.x);
		v2.y = this.y + t * (v.y - this.y);
		v2.z = this.z + t * (v.z - this.z);
		return v2;
	}

	public Vector3f interpolate(Vector3f v, Vector3f axis, float t) {
		Vector3f v2 = new Vector3f();
		v2.x = axis.x * (v.x - this.x) * (t - 1) + v.x;
		v2.y = axis.y * (v.y - this.y) * (t - 1) + v.y;
		v2.z = axis.z * (v.z - this.z) * (t - 1) + v.z;
		return v2;
	}

	public Vector3f flipY() {
		return new Vector3f(this.x, -this.y, this.z);
	}

	public Vector3f flipZ() {
		return new Vector3f(this.x, this.y, -this.z);
	}

	public Vector3f flipX() {
		return new Vector3f(-this.x, this.y, this.z);
	}

	public Vector3f flip() {
		return new Vector3f(-this.x, -this.y, -this.z);
	}
	/*
	 * @Override public boolean equals(Object v) { Vector3f v2 = (Vector3f)v; if
	 * (this.x == v2.x && this.y == v2.y && this.z == v2.z) { return true; } else {
	 * return false; } }
	 */

	@Override
	public float[] asFloat() {
		return new float[] { this.x, this.y, this.z };
	}

	@Override
	public Vector2f toVec2f() {
		return new Vector2f(this.x, this.y);
	}

	@Override
	public Vector3f toVec3f() {
		return this.copy();
	}

	@Override
	public Vector4f toVec4f() {
		return new Vector4f(this.x, this.y, this.z, 0);
	}

	@Override
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z;
	}
}
