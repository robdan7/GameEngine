package core.input.ui;

import java.util.function.Supplier;

import core.input.KeyboardListener;

public class KeyLpointer extends KeyboardListener implements Runnable {
	Supplier<KeyboardListener> action;

	
	public KeyLpointer(Supplier<KeyboardListener> action) {
		this.action = action;
	}
	
	@Override
	public void notifyPress(int key) {
		KeyboardListener listener = this.action.get();
		listener.notifyPress(key);
	}
	
	@Override
	public void notifyRelease(int key) {
		KeyboardListener listener = this.action.get();
		listener.notifyRelease(key);
	}
	
	@Override
	public void notifyHold(int key) {
		KeyboardListener listener = this.action.get();
		listener.notifyHold(key);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
