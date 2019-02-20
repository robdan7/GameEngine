package core.utils.math;

public abstract class Vector {
	public float x,y,z,w=0;

	Vector() {
		this(0,0,0,0);
	}
	
	Vector(float x, float y) {
		this(x,y,0,0);
	}
	
	Vector(float x, float y, float z) {
		this(x,y,z,0);
	}
	
	Vector(float x,float y, float z, float w) {
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
	 * Set the vector's length to 1 and scale to 1. 
	 * @return This instance.
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
	 * Create a normalized copy of a vector.
	 * @param v
	 * @return
	 */
	public static final Vector normalize(Vector v) {
		Vector v2 = v.copy();
		return v2.normalize();
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
	public abstract Vector copy();
	
	
	/**
	 * Copies the numbers from v1 to v2.
	 * @param v1 - The vector to copy.
	 * @param v2
	 */
	public static void copy(Vector v1, Vector v2) {
		v2.x = v1.x;
		v2.y = v1.y;
		v2.z = v1.z;
		v2.w = v1.w;
	}
	
	/**
	 * 
	 * @return A vector with opposite values. Does not effect the original.
	 */
	public final Vector flip() {
		return new Vector4f(-this.x,-this.y,-this.z,-this.w);
	}
	
	public final static Vector flip(Vector v) {
		Vector v2 = v.copy();
		v2.x = -v.x;
		v2.y = -v.y;
		v2.z = -v.z;
		return v2;
	}
	
	public static final Vector flipZ(Vector v) {
		Vector v2 = v.copy();
		v2.z = -v.z;
		return v2;
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
	
	/**
	 * Add two vectors together.
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static final Vector4f add(Vector v1, Vector v2)  {
		return new Vector4f(v1.x+v2.x,v1.y+v2.y,v1.z+v2.z,1);
	}
	
	/**
	 * Subtract a vector from this vector instance.
	 * @param v
	 * @return
	 */
	public final Vector subtract(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	public static final Vector subtract(Vector v1, Vector v2) {
		return new Vector4f(v1.x-v2.x,v1.y-v2.y,v1.z-v2.z,1);
	}
	
	/**
	 * Multiply this vector by a value.
	 * @param m
	 * @return
	 */
	public final Vector multiply(float m) {
		this.z *= m;
		this.x *= m;
		this.y *= m;
		return this;
	}
	
	public final Vector multiply(Vector v) {
		this.x *= v.x;
		this.y *= v.y;
		this.z *= v.z;
		this.w *= v.w;
		return this;
	}
	
	/**
	 * Multiply a vector by a value.
	 * @param v
	 * @param m
	 * @return
	 */
	public static final Vector multiply(Vector v, float m) {
		Vector v2 = new Vector4f();
		v2.x = v.x*m;
		v2.y = v.y*m;
		v2.z = v.z*m;
		v2.w = v.w;
		return v2;
	}
	
	/**
	 * Calculate the cross product of this vector and v in right handed euclidean space.
	 * @param v
	 * @return
	 */
	public final Vector crossProduct(Vector v) {
		if (this.length() == 0) {
			throw new RuntimeException("Length of this vector is 0.");
		}
		Vector v2 = new Vector4f();
		v2.x = this.y * v.z - v.y * this.z;
		v2.y = this.z * v.x - v.z * this.x;
		v2.z = this.x * v.y - v.x * this.y;
		return v2;
	}
	
	/**
	 * Calculate the dot product between this and another vector.
	 * @param v
	 * @return
	 */
	public final float dotProduct(Vector v) {
		return this.x*v.x + this.y*v.y+ this.z*v.z;
	}
	
	/**
	 * Create an interpolated vector from this and a another vector.
	 * @param v
	 * @param t
	 * @return
	 */
	public final Vector interpolate(Vector v, float t) {
		Vector v2 = new Vector4f(0, 0, 0,1);
		v2.x = this.x + t * (v.x - this.x);
		v2.y = this.y + t * (v.y - this.y);
		v2.z = this.z + t * (v.z - this.z);
		return v2;
	}
	
	/**
	 * Create an interpolated vector from this and another vector through a specified axis.
	 * @param v
	 * @param axis
	 * @param t
	 * @return
	 */
	public final Vector interpolate(Vector v, Vector axis, float t) {
		Vector v2 = new Vector4f();
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
	
	
	/**
	 * Generate an array from one or more vectors.
	 * @param vectors - One or more {@link Vector4f}.
	 * @return
	 */
	public static float[] to4fArray(Vector4f... vectors) {
		float[] result = new float[vectors.length*4];
		int i  = 0;
		for (Vector4f v : vectors) {
			result[i] = v.x;
			result[i+1] = v.y;
			result[i+2] = v.z;
			result[i+3] = v.w;
			i+=4;
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector)) {
			throw new RuntimeException("object is not instance of Vector");
		}
		return !((this.x != ((Vector)o).x) || (this.y != ((Vector)o).y) || (this.z != ((Vector)o).z));
	}
}
