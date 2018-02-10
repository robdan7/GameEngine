package utils.math;

public interface Vector {

	/**
	 * Set all variables to 0.
	 */
	public abstract void setZero();
	
	/**
	 * Set the vectors length to 1.
	 */
	public abstract Vector normalize();
	
	/**
	 * 
	 * @return The length of a vector.
	 */
	public abstract float length();
	
	/**
	 * 
	 * @return A copy of a vector.
	 */
	public abstract Vector copy();
	
	/**
	 * 
	 * @return A vector with opposite values. Does not effect the original.
	 */
	public abstract Vector flip();
	
	/**
	 * Add a vector to another. The effect is immediate.
	 * @param v - The vector to add.
	 * @return this.
	 */
	public abstract Vector add(Vector v);
	
	/**
	 * 
	 * @return Return this as a list of floats.
	 */
	public abstract float[] asFloat();
	
	/**
	 * 
	 * @return This as a Vector2f.
	 */
	public abstract Vector2f toVec2f();
	
	/**
	 * 
	 * @return  This as a Vector3f.
	 */
	public abstract Vector3f toVec3f();
	
	/**
	 * 
	 * @return This as a Vector4f.
	 */
	public abstract Vector4f toVec4f();
	
	/**
	 * 
	 * @return This as a string.
	 */
	public abstract String toString();
}
