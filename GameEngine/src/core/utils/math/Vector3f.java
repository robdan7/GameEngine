package core.utils.math;

/**
 * 3-dimensional vector.
 * @author Robin
 *
 */
public class Vector3f extends Vector<Vector3f> {
	//public float x,y,z;
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
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	public void set(Vector<? extends Vector<?>> v) {
		this.set(v.x, v.y, v.z);
	}

	@Override
	public float[] asFloats() {
		return Vector.asFloats(this);
	}

/*
	@Override
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z;
	}
*/
	@Override
	public Vector3f copy() {
		// TODO Auto-generated method stub
		return new Vector3f(this.x, this.y, this.z);
	}

	@Override
	public Vector3f getNormalized() {
		Vector3f v2 = this.copy();
		v2.normalize();
		return v2;
	}
}
