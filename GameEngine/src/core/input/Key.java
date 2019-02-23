package core.input;

public class Key{
	private actionType actiontype;
	public short state;
	private Runnable action, pressAction, releaseAction, holdAction;
	
	
	/**
	 * 
	 * @param actiontype - True = only activate on on press.
	 * @param action - Runnable 
	 */
	public Key(actionType actiontype, Runnable action) {
		this.actiontype = actiontype;
		//this.action = action;
	}
	
	public Key(actionType actionType) {
		this.actiontype = actionType;
	}
	
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
	 * @return The key type. true is equal to press only. false is press and/or hold.
	 *
	 */
	public actionType getType() {
		return this.actiontype;
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
	
	/**
	 *  Run action associated with the key.
	 */
	@Deprecated
	public void run() {
		action.run();
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
