package core.utils.math;

/**
 * 4-dimensional vector.
 * @author Robin
 *
 */
public class Vector4f extends Vector<Vector4f> {
	public Vector4f() {
		super();
	}

	public Vector4f(float x, float y, float z, float w) {
		super(x,y,z,w);
	}
	
	@Override
	public void set(Vector4f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}

	@Override
	public Vector4f copy() {
		return new Vector4f(this.x, this.y, this.z, this.w);
	}

	@Override
	public Vector4f create() {
		return new Vector4f();
	}
}
