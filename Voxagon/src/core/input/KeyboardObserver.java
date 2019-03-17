package core.input;

import core.utils.event.Observer;

/**
 * This is a place holder for any class that can
 * notify listeners when a key, on the keyboard, has been
 * activated.
 * @author Robin
 *
 */
public abstract class KeyboardObserver extends Observer<Key, KeyboardObserver, KeyboardListener>{
	
	/**
	 * A key has initially been pressed, notify all listeners 
	 * with the proper method call.
	 * @param key - The key index
	 * @param arg
	 */
	protected abstract void notifyKeyPress(int key, Object arg);
	
	
	/**
	 * A key has been released, notify all listeners
	 * with the proper method call.
	 * @param key - The key index
	 * @param arg
	 */
	protected abstract void notifyKeyRelease(int key, Object arg);
	
	/**
	 * A key has been held down for a longer period, 
	 * notify all listeners.
	 * @param key - The key index
	 * @param arg
	 */
	protected abstract void notifyKeyHold(int key, Object arg);

}
