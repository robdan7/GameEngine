package core.utils.math;

public abstract class Vector<T extends Vector<T>> {
	protected float x,y,z,w;

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
	 * Set the vector's length to 1.
	 * @return This instance.
	 */
	public final void normalize() {
		float length = this.length();
		if (length != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
			this.z = this.z / length;
		}
	}
	
	/**
	 * Get a normalized copy of this vector.
	 * @return A normalized copy.
	 */
	public abstract T getNormalized();
	
	

	/**
	 * 
	 * @return The length of a vector.
	 */
	public final float length() {
		return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	/**
	 * 
	 * @return A copy of this vector.
	 */
	public abstract T copy();
	
	/**
	 * 
	 * @return A vector with opposite values. Does not effect the original.
	 */
	public void  flip() {
		this.x = -x;
		this.y = -y;
		this.z = -z;
	}
	
	public T asFlipped() {
		T v = this.copy();
		v.flip();
		return v;
	}
	
	
	/**
	 * Add a vector to another. The effect is immediate.
	 * @param v - The vector to add.
	 * @return this.
	 */
	public final void add(Vector<T> v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	
	/**
	 * Subtract a vector from this vector instance.
	 * @param v
	 * @return
	 */
	public final void subtract(Vector<T> v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	
	/**
	 * Multiply this vector by a value.
	 * @param m
	 * @return
	 */
	public void multiply(float m) {
		this.z *= m;
		this.x *= m;
		this.y *= m;
	}
	
	public void multiply(Vector<T> v) {
		this.x *= v.x;
		this.y *= v.y;
		this.z *= v.z;
		this.w *= v.w;
	}
	
	public T asMultiplied(T v) {
		T result = this.copy();
		v.multiply(v);
		return result;
	}
	
	public T asMultiplied(float m) {
		T result = this.copy();
		result.multiply(m);
		return result;
	}
	
	/**
	 * Calculate the cross product of this vector and v in right handed euclidean space.
	 * @param v
	 * @return
	 */
	public final Vector4f crossProduct(Vector<T> v) {
		if (this.length() == 0) {
			throw new RuntimeException("Length of this vector is 0.");
		}
		Vector4f v2 = new Vector4f();
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
	public final float dotProduct(Vector<T> v) {
		return this.x*v.x + this.y*v.y+ this.z*v.z;
	}
	
	/**
	 * Create an interpolated vector from this and a another vector.
	 * @param v
	 * @param t
	 * @return
	 */
	public final Vector4f interpolate(Vector<T> v, float t) {
		Vector4f v2 = new Vector4f(0, 0, 0,1);
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
	public final Vector4f interpolate(Vector<T> v, Vector<T> axis, float t) {
		Vector4f v2 = new Vector4f();
		v2.x = axis.x * (v.x - this.x) * (t - 1) + v.x;
		v2.y = axis.y * (v.y - this.y) * (t - 1) + v.y;
		v2.z = axis.z * (v.z - this.z) * (t - 1) + v.z;
		return v2;
	}
	
	/**
	 * 
	 * @return Return this as a list of floats.
	 */
	public abstract float[] asFloats();
	
	public static float[] asFloats(Vector2f v) {
		return new float[] {v.x,v.y};
	}
	
	public static float[] asFloats(Vector3f v) {
		return new float[] {v.x,v.y,v.z};
	}
	
	public static float[] asFloats(Vector4f v) {
		return new float[] {v.x,v.y,v.z,v.w};
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
		return !((this.x != ((Vector<?>)o).x) || (this.y != ((Vector<?>)o).y) || (this.z != ((Vector<?>)o).z));
	}
}
