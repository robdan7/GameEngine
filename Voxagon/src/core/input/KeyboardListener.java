package core.input;

import core.input.Key.KeyAction;
import core.utils.event.Listener;
import core.utils.event.Observer;

public class KeyboardListener implements Listener<Key, KeyboardListener, KeyboardObserver>{
	
	private KeyAction[] keys;
	
	KeyboardListener() {
		this.keys = new KeyAction[65536];
	}
	
	public void notifyPress(int key) {
		if (this.keys[key] != null) this.keys[key].onPress();
	}
		
	public void notifyRelease(int key) {
		if (this.keys[key] != null) this.keys[key].onRelease();
	}
	
	public void notifyHold(int key) {
		// TODO Create a hold function for the keys.
	}
	
	void addKeyReleaseFunction(int key, Runnable action) {
		if (this.keys[key] == null) {
			this.keys[key] = new KeyAction();
		}
		this.keys[key].setReleaseAction(action);
	}
	
	void addKeyPressFunction(int key, Runnable action) {
		if (this.keys[key] == null) {
			this.keys[key] = new KeyAction();
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
