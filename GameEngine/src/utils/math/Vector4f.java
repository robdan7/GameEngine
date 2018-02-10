package utils.math;

public class Vector4f implements Vector {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public void setZero() {
		// TODO Auto-generated method stub
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}

	@Override
	public Vector normalize() {
		// TODO Auto-generated method stub
		float length = this.length();
		if (this.length() != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
			this.z = this.z / length;
		}
		return this;
	}

	@Override
	public Vector add(Vector v) {
		return new Vector4f(this.x + ((Vector3f) v).x, this.y + ((Vector3f) v).y, this.z + ((Vector3f) v).z,
				this.w + ((Vector4f) v).w);
	}

	@Override
	public float length() {
		// TODO Auto-generated method stub
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z) * this.w;
	}

	@Override
	public float[] asFloat() {
		return new float[] { this.x, this.y, this.z, this.w };
	}

	@Override
	public Vector copy() {
		return new Vector4f(this.x, this.y, this.z, this.w);
	}

	@Override
	public Vector2f toVec2f() {
		return new Vector2f(this.x, this.y);
	}

	@Override
	public Vector3f toVec3f() {
		return new Vector3f(this.x, this.y, this.z);
	}

	@Override
	public Vector4f toVec4f() {
		return (Vector4f)this.copy();
	}

	@Override
	public Vector flip() {
		return new Vector4f(-this.x, -this.y, -this.z, this.w);
	}

	@Override
	public String toString() {
		return this.x + " : " + this.y + " : " + this.z + " : " + this.w;
	}
}
