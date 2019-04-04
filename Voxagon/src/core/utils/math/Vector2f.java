package core.utils.math;

/**
 * 2-dimensional vector.
 * @author Robin
 *
 */
public class Vector2f extends Vector<Vector2f>{
	public static final int SIZE= 2;
	
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
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void set(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	@Override
	public void set(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
	}

	@Override
	public void set(Vector4f v) {
		this.x = v.x;
		this.y = v.y;
	}

	@Override
	public float[] asFloats() {
		return new float[] {this.x, this.y};
	}
	
	@Override
	public String toString() {
		return this.x + " , " + this.y;
	}

	@Override
	public Vector2f copy() {
		// TODO Auto-generated method stub
		return new Vector2f(this.x, this.y);
	}

	@Override
	public Vector2f create() {
		return new Vector2f();
	}
}
