package core.utils.math;

public class Vector2f extends Vector{
	
	public Vector2f() {
		super();
	}
	
	public Vector2f(float x, float y) {
		super(x,y);
	}

	@Override
	public float[] asFloat() {
		return new float[] {this.x, this.y};
	}
	
	@Override
	public String toString() {
		return this.x + " : " + this.y;
	}
}
