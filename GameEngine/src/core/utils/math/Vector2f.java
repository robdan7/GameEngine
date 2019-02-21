package core.utils.math;

/**
 * 2-dimensional vector.
 * @author Robin
 *
 */
public class Vector2f extends Vector<Vector2f>{
	//public float x,y;
	public Vector2f() {
		super();
	}
	
	public Vector2f(float x, float y) {
		super(x,y);
	}
	
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void set(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void set(Vector2f v) {
		this.set(v.x, v.y);
	}

	@Override
	public float[] asFloats() {
		return Vector.asFloats(this);
	}
	/*
	@Override
	public String toString() {
		return this.x + " : " + this.y;
	}
*/
	@Override
	public Vector2f copy() {
		// TODO Auto-generated method stub
		return new Vector2f(super.x, super.y);
	}

	@Override
	public Vector2f getNormalized() {
		Vector2f v2 = (Vector2f) this.copy();
		v2.normalize();
		return v2;
	}
}
