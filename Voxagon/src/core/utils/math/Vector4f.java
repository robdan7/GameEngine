package core.utils.math;

/**
 * 4-dimensional vector.
 * @author Robin
 *
 */
public class Vector4f extends Vector<Vector4f> {
	public static final int SIZE = 4;
	public Vector4f() {
		super();
	}

	public Vector4f(float x, float y, float z, float w) {
		super(x,y,z,w);
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
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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

	@Override
	public float[] asFloats() {
		return new float[] {this.x,this.y,this.z,this.w};
	}

	@Override
	public float[] toFloatArray() {
    	return new float[] {this.getX(),this.getY(),this.getZ(),this.getW()};
	}
}
