package core.input;

/**
 * This class holds the internal key state.
 * @author Robin
 *
 */
public class Key{
	public short state;
	private Runnable pressAction, releaseAction, holdAction;
	
	
	public Key() {
	}
	
	/**
	 * 
	 * @return Return true if the key is either pressed or held down.
	 */
	public boolean isPressed() {
		return (this.state != 0);
	}
	
	/**
	 * 
	 * @return The state of the key. 0 = released, 1 = pressed, 2 = held down.
	 *
	 */
	public short getState() {
		return this.state;
	}
	
	/**
	 * 
	 * @param state - The state to set.
	 */
	void setState(int state) {
		this.state = (short)state;
	}
	

	void setPressAction(Runnable action) {
		this.pressAction = action;
	}
	

	void setHoldAction(Runnable action) {
		
	}
	
	
	void setReleaseAction(Runnable action ) {
		this.releaseAction = action;
	}

	
	public void onPress() {
		if (this.pressAction != null) {
			this.pressAction.run();
		}
	}
	
	
	public void onRelease() {
		if (this.releaseAction != null) {
			this.releaseAction.run();
		}
	}
	
	@Deprecated
	public void onHold() {
		if (this.holdAction != null) {
			this.holdAction.run();
		}
	}
	
	@Deprecated
	public static enum actionType {
		TYPE, HOLD
		
	}
}
