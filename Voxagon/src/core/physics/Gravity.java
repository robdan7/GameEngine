package core.physics;

public class Gravity {
	private static float g = 9.806f;
	private float localG = g;
	
	/**
	 * Create a local gravity object.
	 * @param g
	 */
	public Gravity(float g) {
		this.localG = g;
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
