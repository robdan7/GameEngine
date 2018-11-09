package core.utils.math;

public class Vector4f extends Vector {
	public Vector4f() {
		super();
	}

	public Vector4f(float x, float y, float z, float w) {
		super(x,y,z,w);
	}

	@Override
	public Vector copy() {
		return new Vector4f(this.x, this.y, this.z, this.w);
	}
}
