package core.physics.geometry;

import core.utils.math.Vector;
import core.utils.math.Vector3f;

public class Circle {
	private float radius;
	Vector3f position;
	
	public Circle(float radius) {
		this.radius = radius;
	}
	
	public void setRadius(float r) {
		this.radius = r;
	}
	
	/**
	 * Set the position.
	 * @param position
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	/**
	 * Get the position.
	 * @return
	 */
	public Vector3f getPositionCopy() {
		return this.position.copy();
	}

}
