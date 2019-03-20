package core.input;

import core.utils.event.Observer;
import core.utils.math.Vector2f;

public class InputInterface {
	private KeyboardListener keyboard;
	private MouseListener mouse;

	
	protected InputInterface(MouseListener m) {
		this.mouse = m;
		this.keyboard = new KeyboardListener();
	}
	
	protected KeyboardListener getKeyboardListener() {
		return this.keyboard;
	}

	protected MouseListener getMouseListener() {
		return this.mouse;
	}
	
	protected void releaseAllButtons() {
		for (int i = 0; i < Keyboard.KEYS; i++) {
			keyboard.notifyRelease(i);
		}
	}
	
	public void addKeyReleaseFunction(int key, Runnable action) {
		this.keyboard.addKeyReleaseFunction(key, action);
	}
	
	public void addKeyPressFunction(int key, Runnable action) {
		this.keyboard.addKeyPressFunction(key, action);
	}
	
	public void addKeyHoldFunction(int key, Runnable action) {
		this.keyboard.addKeyHoldFunction(key, action);
	}
}
