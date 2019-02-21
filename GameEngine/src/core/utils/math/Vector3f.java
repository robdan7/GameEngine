package core.utils.math;

/**
 * 3-dimensional vector.
 * @author Robin
 *
 */
public class Vector3f extends Vector<Vector3f> {

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
	
	@Override
	public void set(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}


	@Override
	public float[] asFloat() {
		return new float[] { this.x, this.y, this.z };
	}


	@Override
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z;
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
}
