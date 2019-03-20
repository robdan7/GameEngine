package core.physics;

import core.utils.math.Vector3f;

public class Kinematics {

	public Kinematics() {
		// TODO Auto-generated constructor stub
	}
	
	public static float calcLinearSpeed(float time, float acceleration, float V0) {
		return V0 + time*acceleration;
	}
	
	/**
	 * 
	 * @param time - The time that has passed.
	 * @param acceleration - Linear acceleration.
	 * @param velocity - Start velocity.
	 * @param position - Start position.
	 * @return
	 */
	public static Vector3f calcLinearMotion(float time, Vector3f acceleration, Vector3f velocity, Vector3f position) {
		Vector3f deltaS = acceleration.asMultiplied((float)(Math.pow(time, 2)/ 2.0f));
		Vector3f result = position.copy();
		result.add(velocity.asMultiplied(time));
		result.add(deltaS);
		return result;
	}
	
	public static Vector3f calcLinearMotion(float time, Vector3f velocity, Vector3f position) {
		Vector3f result = position.copy();
		result.add(velocity.asMultiplied(time));
		return result;
	}
	
	/**
	 * Calculate the rotational motion during time t around a fixed point.
	 * @param t
	 * @param acceleration
	 * @param velocity
	 * @param position
	 * @param center
	 * @return
	 */
	public static Vector3f calcRotationalMotion(float t, Vector3f acceleration, Vector3f velocity, Vector3f position, Vector3f center) {
		
		return null;
	}

}
