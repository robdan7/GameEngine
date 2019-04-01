package core.utils.math;

public abstract  class Vector<T extends Vector<T>> {
	protected float x,y,z,w=0;

	protected Vector() {
		this(0,0,0,0);
	}
	
	protected Vector(float x, float y) {
		this(x,y,0,0);
	}
	
	protected Vector(float x, float y, float z) {
		this(x,y,z,0);
	}
	
	protected Vector(float x,float y, float z, float w) {
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
	 * Set the values of this vector to be the same as another.
	 * @param v
	 */
	public abstract void set(Vector2f v);
	
	/**
	 * Set the values of this vector to be the same as another.
	 * @param v
	 */
	public abstract void set(Vector3f v);
	
	/**
	 * Set the values of this vector to be the same as another.
	 * @param v
	 */
	public abstract void set(Vector4f v);
	
	/**
	 * Create a new vector of the same data type.
	 * @return - A new vector.
	 */
	public abstract T create();
	
	/**
	 * Set the vector's length to 1 and scale to 1. 
	 * @return This instance.
	 */
	public final void normalize() {
		float length = this.length();
		if (this.length() != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
			this.z = this.z / length;
		}
	}
	
	
	public T asNormalized() {
		T v2 = this.copy();
		v2.normalize();
		return v2;
	}	

	/**
	 * 
	 * @return The length of a vector.
	 */
	public final float length() {
		return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	/**
	 * Make a copy of this vector.
	 * @return A copy of a vector.
	 */
	public abstract T copy();
	
	/**
	 * Flip the axes of this vector.
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
	 */
	public void add(T v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	/**
	 * Subtract a vector from this vector.
	 * @param v
	 * @return
	 */
	public final void subtract(T v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	
	/**
	 * Return a copy of this vector subtracted by v.
	 * @param v - The vector to subtract from.
	 * @return A subtracted copy of this vector.
	 */
	public T asSubtracted(T v) {
		T result = this.copy();
		result.subtract(v);
		return result;
	}
	
	/**
	 * Return a copy of this vector added to v.
	 * @param v - The vector to add.
	 * @return This vector plus v.
	 */
	public T asAdded(T v) {
		T result = this.copy();
		result.add(v);
		return result;
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
	
	/**
	 * Multiply this vector by a vector v.
	 * @param v - The vector to multiply with.
	 */
	public void multiply(T v) {
		this.x *= v.x;
		this.y *= v.y;
		this.z *= v.z;
		this.w *= v.w;
	}
	
	/**
	 * Return a copy of this vector multiplied by v.
	 * @param v - The vector to multiply with.
	 * @return - A multiplied copy.
	 */
	public T asMultiplied(T v) {
		T result = this.copy();
		result.multiply(v);
		return  result;
	}
	
	/**
	 * Return a copy of this vector multiplied by a constant.
	 * @param f - The constant to multiply with.
	 * @return A multiplied copy.
	 */
	public T asMultiplied(float f) {
		T result = this.copy();
		result.multiply(f);
		return result;
	}
	
	
	/**
	 * Calculate the cross product of this vector and v in right handed euclidean space. 
	 * No error is thrown if the resulting vector is the zero vector.
	 * @param v
	 * @return
	 */
	public T crossProduct(T v) {
		T v2 = this.copy();
		v2.x = this.y * v.z - v.y * this.z;
		v2.y = this.z * v.x - v.z * this.x;
		v2.z = this.x * v.y - v.x * this.y;
		/*
		if (v2.length() == 0) {
			v2.set(new Vector3f((float)Math.random(), (float)Math.random(),(float)Math.random()));
			v2.add(v);
			return crossProduct(v2);
		}
		*/
		
		return v2;
	}
	
	/**
	 * Calculate the dot product between this and another vector.
	 * @param v
	 * @return
	 */
	public float dot(T v) {
		return this.x*v.x + this.y*v.y+ this.z*v.z;
	}
	
	/**
	 * Create an interpolated vector from this and a another vector.
	 * @param v
	 * @param t
	 * @return
	 */
	public T interpolate(T v, float t) {
		T v2 = this.create();
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
	public final T interpolate(T v, Vector3f axis, float t) {
		T v2 = this.create();
		v2.x = axis.x * (v.x - this.x) * (t - 1) + v.x;
		v2.y = axis.y * (v.y - this.y) * (t - 1) + v.y;
		v2.z = axis.z * (v.z - this.z) * (t - 1) + v.z;
		return v2;
	}
	
	/**
	 * 
	 * @return Return this as an array of floats.
	 */
	public abstract float[] asFloats();
	
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
		return this.x + " , " + this.y + " , " + this.z + " , " + this.w;
	}
	
	
	/**
	 * 
	 * @return This vector as an array;
	 */
	public abstract float[] toFloatArray();
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector)) {
			throw new RuntimeException("object is not instance of Vector");
		}
		return !((this.x != ((Vector<?>)o).x) || (this.y != ((Vector<?>)o).y) || (this.z != ((Vector<?>)o).z));
	}
}
