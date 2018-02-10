package utils.math;

/**
 * 
 * @author Robin
 *
 */
public class Vector3f extends Vector {

	public Vector3f() {
		super();
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
		super(x,y,z);
	}


	@Override
	public float[] asFloat() {
		return new float[] { this.x, this.y, this.z };
	}


	@Override
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z;
	}
}
