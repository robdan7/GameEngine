package core.physics.mechanics;

import core.utils.math.Vector3f;

/**
 * This class represents a position buffer for storing data without
 * deleting what was previously stored. A position can be buffered 
 * and then stored as the target position whenever needed.
 * @author Robin
 *
 */
public class BufferedMotionContainer {
	private final Vector3f bufferedPosition, targetPosition;

	public BufferedMotionContainer(Vector3f targetPosition) {
		this.targetPosition = targetPosition;
		this.bufferedPosition = new Vector3f();
	}
	
	public BufferedMotionContainer() {
		this.targetPosition = new Vector3f();
		this.bufferedPosition = new Vector3f();
	}
	
	public Vector3f getTargetPosition() {
		return this.targetPosition;
	}
	
	public Vector3f getBufferedPosition() {
		return this.bufferedPosition;
	}
	
	/**
	 * Store a vector as the buffered position. This is required before unloading.
	 * @param v - The vector to copy.
	 */
	public void storeBufferedPosition(Vector3f v) {
		this.bufferedPosition.set(v);
	}
	
	public void storeBufferedPosition(float x, float y) {
		this.bufferedPosition.set(x,y);
	}
	
	public void storeBufferedPositionX(float x) {
		this.bufferedPosition.setX(x);
	}
	
	public void storeBufferedPositionY(float y) {
		this.bufferedPosition.setY(y);
	}
	
	public void storeBufferedPositionZ(float z) {
		this.bufferedPosition.setY(z);
	}

	/**
	 * Copy the values of the buffered position onto the target position. 
	 * The effect is immediate.
	 */
	public void unloadBufferedPosition() {
		this.targetPosition.set(this.bufferedPosition);
	}
	
	/**
	 * Resets the buffered position to the target. The objects will not be equal.
	 */
	public void resetBuffer() {
		this.bufferedPosition.set(this.targetPosition);
	}
	
	

}
