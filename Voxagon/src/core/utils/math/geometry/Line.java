package core.utils.math.geometry;

import core.utils.math.Vector3f;

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
	public Line(Vector3f start, Vector3f pointer) {
		this.start = start.toVec3f();
		this.pointer = pointer.toVec3f();
	}
	
	/**
	 * get a translated point from the beginning of this line.
	 * @param t - The length to translate parallel to the line.
	 * @return
	 */
	public Vector3f getTranslated(float t) {
		return new Vector3f(this.start.getX() + this.pointer.getX()*t,this.start.getY() + this.pointer.getY()*t,this.start.getZ() + this.pointer.getZ()*t);
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
	public void setDirection(Vector3f direction) {
		this.pointer.set(direction);
	}
	
	/**
	 * Set the starting point of this line.
	 * @param start
	 */
	public void setStart(Vector3f start) {
		this.start.set(start);
	}
}
