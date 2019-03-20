package core.input;

import core.input.Key;
import core.utils.event.Listener;
import core.utils.event.Observer;

public class KeyboardListener implements Listener<Key, KeyboardListener, KeyboardObserver>{
	
	private Key[] keys;
	
	public KeyboardListener() {
		
		this.keys = new Key[Keyboard.KEYS];
	}
	
	/**
	 * Launch the press action if the key state is changed. I.e. 
	 * the action is only activated once if the notification is repeated.
	 * @param key
	 */
	public void notifyPress(int key) {
		if (this.keys[key] != null && keys[key].getState() != 1) {
			this.keys[key].onPress();
			keys[key].setState(1);
		}
	}
		
	
	/**
	 * Launch the release action if the key state is changed. I.e.
	 * the action is only activated once if the notification is repeated.
	 * @param key
	 */
	public void notifyRelease(int key) {
		if (this.keys[key] != null && keys[key].getState() != 0) {
			this.keys[key].onRelease();
			this.keys[key].setState(0);
		}
	}
	
	public void notifyHold(int key) {
		// TODO Create a hold function for the keys.
	}
	
	void addKeyReleaseFunction(int key, Runnable action) {
		if (this.keys[key] == null) {
			this.keys[key] = new Key();
		}
		this.keys[key].setReleaseAction(action);
	}
	
	void addKeyPressFunction(int key, Runnable action) {
		if (this.keys[key] == null) {
			this.keys[key] = new Key();
		}
		this.keys[key].setPressAction(action);
	}
	
	@Deprecated
	void addKeyHoldFunction(int key, Runnable action) {
		throw new UnsupportedOperationException("This method is not supported (yet)");
	}


	@Override
	public void update(Observer<Key, KeyboardObserver, KeyboardListener> b, Key arg) {
		switch (arg.getState()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}



}
