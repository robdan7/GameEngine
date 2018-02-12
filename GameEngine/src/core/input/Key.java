package core.input;

public class Key{
	private boolean actiontype;
	public short state;
	private Runnable action;
	
	
	/**
	 * 
	 * @param actiontype - True = only activate on on press.
	 * @param action - Runnable 
	 */
	public Key(boolean actiontype, Runnable action) {
		this.actiontype = actiontype;
		this.action = action;
	}
	
	public Key( Runnable action) {
		this.actiontype = false;
		this.action = action;
	}
	
	/**
	 * 
	 * @return Return true if the key is either pressed or held down.
	 */
	public boolean getPress() {
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
	public boolean getType() {
		return actiontype;
	}
	
	/**
	 * 
	 * @param state - The state to set.
	 */
	public void setState(int state) {
		this.state = (short)state;
	}
	
	/**
	 *  Run action associated with the key.
	 */
	public void run() {
		action.run();
	}
}
