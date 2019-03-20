package core.physics;

import core.utils.math.Vector3f;

public class Gravity {
	private static float g = 9.806f;
	private float localG;
	
	private Vector3f upVector, acceleration, currentVelocity;
	
	/**
	 * 
	 * @param g
	 * @param upVector - A normalized vector.
	 */
	public Gravity(float g, Vector3f upVector) {
		this.localG = g;
		this.upVector = upVector.asNormalized();
		this.acceleration = this.upVector.asMultiplied(-this.localG);
		this.currentVelocity = new Vector3f();
	}
	
	public Gravity(Vector3f direction) {
		this.localG = g;
		this.upVector = direction.asNormalized();
		this.acceleration = this.upVector.asMultiplied(-this.localG);
		this.currentVelocity = new Vector3f();
	}
	
	/**
	 * Get the current fall velocity.
	 * @return
	 */
	public float getVelocity() {
		return this.currentVelocity.length();
	}
	
	/**
	 * Set the current fall velocity.
	 * @param v
	 */
	public void setVelocity(float v) {
		this.currentVelocity = this.upVector.asMultiplied(v);
	}
	
	public Vector3f calcFallDistance(float time) {
		Vector3f result = Kinematics.calcLinearMotionDelta(time, this.acceleration, this.currentVelocity);
		this.currentVelocity.add(this.acceleration.asMultiplied(time));
		return result;
	}
	
	public void resetFallVelocity() {
		this.currentVelocity.setZero();
	}
	
	/**
	 * Set the local gravity. This does not affect the global gravity.
	 * @param g
	 */
	public void setLocalGravity(float g) {
		this.localG = g;
	}
	
	
	/**
	 * @return The local gravity constant.
	 */
	public float getLocalGravity() {
		return this.localG;
	}
	
	/**
	 * Set the global gravitational constant.
	 * @param g
	 */
	public static void setGlobalGravity(float g) {
		Gravity.g = g;
	}
	
	/**
	 * 
	 * @return The global gravitational constant.
	 */
	public static float getGlobalGravity() {
		return Gravity.g;
	}

}
