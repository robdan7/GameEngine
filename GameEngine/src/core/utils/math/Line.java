package core.utils.math;

/**
 * this class represents an infinite line in 3D space.
 * @author Robin
 *
 */
public class Line {
	private Vector3f start, pointer;
	
	/**
	 * Create a line from a starting point and a forward vector.
	 * @param start - The starting point.
	 * @param pointer - A normalized vector.
	 */
	public Line(Vector start, Vector pointer) {
		this.start = start.toVec3f();
		this.pointer = pointer.toVec3f();
	}
	
	/**
	 * get a translated point from the beginning of this line.
	 * @param t - The length to translate parallel to the line.
	 * @return
	 */
	public Vector3f getTranslated(float t) {
		return new Vector3f(this.start.x + this.pointer.x*t,this.start.y + this.pointer.y*t,this.start.z + this.pointer.z*t);
	}
	
	public Vector3f getStart() {
		return this.start;
	}
	
	public Vector3f getPointer() {
		return this.pointer;
	}
	
	/**
	 * Set the direction of this line.
	 * @param direction - A normalized vector.
	 */
	public void setDirection(Vector direction) {
		this.pointer.x = direction.x;
		this.pointer.y = direction.y;
		this.pointer.z = direction.z;
	}
	
	/**
	 * Set the starting point of this line.
	 * @param start
	 */
	public void setStart(Vector start) {
		this.start.x = start.x;
		this.start.y = start.y;
		this.start.z = start.z;
	}
}
