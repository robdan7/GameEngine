package core.utils.math.geometry;

import core.utils.math.Vector;
import core.utils.math.Vector3f;

public class Sphere {
	private float radius;
	Vector3f position;

	
	public Sphere(Vector3f position, float radius) {
		this.position = position;
		this.radius = radius;
	}
	
	public Sphere(float radius) {
		this(new Vector3f(), radius);
	}

	
	public void setRadius(float r) {
		this.radius = r;
	}
	
	public float getRadius() {
		return this.radius;
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
	public Vector3f getPosition() {
		return this.position;
	}

}
