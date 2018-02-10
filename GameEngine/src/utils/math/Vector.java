package utils.math;

public class Vector {
	public float x,y,z,w;

	public Vector() {
		this(0,0,0,0);
	}
	
	public Vector(float x, float y) {
		this(x,y,0,0);
	}
	
	public Vector(float x, float y, float z) {
		this(x,y,z,0);
	}
	
	public Vector(float x,float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Set all variables to 0.
	 */
	public final void setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}
	
	/**
	 * Set the vectors length to 1.
	 */
	public final Vector normalize() {
		float length = this.length();
		if (length != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
			this.z = this.z / length;
		}
		return this;
	}
	

	/**
	 * 
	 * @return The length of a vector.
	 */
	public final float length() {
		return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	/**
	 * 
	 * @return A copy of a vector.
	 */
	public final Vector copy() {
		return new Vector(this.x,this.y,this.z,this.w);
	}
	
	/**
	 * 
	 * @return A vector with opposite values. Does not effect the original.
	 */
	public final Vector flip() {
		return new Vector(-this.x,-this.y,-this.z,-this.w);
	}
	
	/**
	 * Add a vector to another. The effect is immediate.
	 * @param v - The vector to add.
	 * @return this.
	 */
	public final Vector add(Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public final Vector subtract(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	public final Vector multiply(float m) {
		this.z *= m;
		this.x *= m;
		this.y *= m;
		this.w *= m;
		return this;
	}
	
	public final Vector crossProduct(Vector v) {
		Vector v2 = new Vector();
		v2.x = this.y * v.z - v.y * this.z;
		v2.y = this.z * v.x - v.z * this.x;
		v2.z = this.x * v.y - v.x * this.y;
		if (this.length() == 0) {
			System.err.println("Vector cross product is zero.");
		}
		return v2;
	}
	
	public final Vector interpolate(Vector v, float t) {
		Vector v2 = new Vector(0, 0, 0);
		v2.x = this.x + t * (v.x - this.x);
		v2.y = this.y + t * (v.y - this.y);
		v2.z = this.z + t * (v.z - this.z);
		return v2;
	}
	
	public final Vector interpolate(Vector v, Vector axis, float t) {
		Vector v2 = new Vector();
		v2.x = axis.x * (v.x - this.x) * (t - 1) + v.x;
		v2.y = axis.y * (v.y - this.y) * (t - 1) + v.y;
		v2.z = axis.z * (v.z - this.z) * (t - 1) + v.z;
		return v2;
	}
	
	/**
	 * 
	 * @return Return this as a list of floats.
	 */
	public float[] asFloat() {
		return new float[] { this.x, this.y, this.z, this.w };
	}
	
	/**
	 * 
	 * @return This as a Vector2f.
	 */
	public Vector2f toVec2f() {
		return new Vector2f(this.x,this.y);
	}
	
	/**
	 * 
	 * @return  This as a Vector3f.
	 */
	public Vector3f toVec3f() {
		return new Vector3f(this.x,this.y,this.z);
	}
	
	/**
	 * 
	 * @return This as a Vector4f.
	 */
	public Vector4f toVec4f() {
		return new Vector4f(this.x,this.y,this.z,this.w);
	}
	
	/**
	 * 
	 * @return This as a string.
	 */
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z + " : " + this.w;
	}
}
