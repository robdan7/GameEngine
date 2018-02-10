package utils.math;

public class Vector2f implements Vector{
	public float x;
	public float y;
	
	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void setZero() {
		this.x = 0;
		this.y = 0;
	}

	@Override
	public Vector normalize() {
		float length = this.length();
		if (this.length() != 0) {
			this.x = this.x/length;
			this.y = this.y/length;
		}
		return this;
	}

	@Override
	public float length() {
		return (float)Math.sqrt(this.x*this.x+this.y*this.y);
	}

	@Override
	public Vector add(Vector v) {
		return new Vector2f(this.x+((Vector2f)v).x,this.y+((Vector2f)v).y);
	}

	@Override
	public float[] asFloat() {
		return new float[] {this.x, this.y};
	}

	@Override
	public Vector2f toVec2f() {
		return (Vector2f)this.copy();
	}

	@Override
	public Vector3f toVec3f() {
		return new Vector3f(this.x,this.y,0);
	}

	@Override
	public Vector4f toVec4f() {
		return new Vector4f(this.x,this.y,0,0);
	}

	@Override
	public Vector copy() {
		return new Vector2f(this.x, this.y);
	}

	@Override
	public Vector flip() {
		return new Vector2f(-this.x,-this.y);
	}
	
	@Override
	public String toString() {
		return this.x + " : " + this.y;
	}
}
