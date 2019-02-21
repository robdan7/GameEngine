package core.utils.math;

/**
 * 4-dimensional vector.
 * @author Robin
 *
 */
public class Vector4f extends Vector<Vector4f> {
	//public float x,y,z,w;
	public Vector4f() {
		super();
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
	
	public float getW() {
		return this.w;
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
	
	public void setW(float w) {
		this.w = w;
	}
	
	public void set(float x, float y, float z, float w) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setW(w);
	}
	
	public void set(Vector4f v) {
		this.set(v.x, v.y, v.z, v.w);
	}
	
	public Vector4f(float x, float y, float z, float w) {
		super(x,y,z,w);
	}
	
	public float[] asFloats() {
		return Vector.asFloats(this);
	}

	@Override
	public Vector4f copy() {
		return new Vector4f(this.x, this.y, this.z, this.w);
	}

	@Override
	public Vector4f getNormalized() {
		Vector4f v2 = this.copy();
		v2.normalize();
		return v2;
	}
}
