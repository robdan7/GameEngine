package core.input;

import core.utils.event.Observer;
import core.utils.math.Vector2f;

public abstract class InputInterface {
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
	
	protected void addKeyReleaseFunction(int key, Runnable action) {
		this.keyboard.addKeyReleaseFunction(key, action);
	}
	
	protected void addKeyPressFunction(int key, Runnable action) {
		this.keyboard.addKeyPressFunction(key, action);
	}
	
	protected void addKeyHoldFunction(int key, Runnable action) {
		this.keyboard.addKeyHoldFunction(key, action);
	}
	
	public void bindToKeyBoard(KeyboardObserver obs) {
		obs.addListener(this.keyboard);
	}
}
