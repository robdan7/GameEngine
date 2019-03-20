package core.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import core.engine.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This is a keyboard controller for GLFW. It is necessary 
 * to set this up before any key callback can be generated.
 * @author Robin
 *
 */
public class Keyboard extends KeyboardObserver {
	
	private GLFWKeyCallback callback;
	
	public final static int KEYS = 65536;
	
	private int[] keys;
	
	/**
	 * Create a keyboard controller.
	 * @param window
	 */
	public Keyboard(Window window) {
		keys = new int[KEYS];		// Total set of keys supported.
		
		this.callback = new GLFWKeyCallback() {
			
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key >= 0) {	// Key is negative when performing certain shortcuts.
					switch(action) {
					case 0:
						//keys[key].onRelease();
						notifyKeyRelease(key, null);
						break;
					case 1:
						//keys[key].onPress();
						notifyKeyPress(key, null);
						break;
					case 2:
						if (keys[key] != 2) {
							//keys[key].onHold();
							notifyKeyHold(key, null);
						}
						break;
					default:
							break;
					}
					keys[key] = action;
				}
			}
			
		};
		
		glfwSetKeyCallback(window.getWindow(), this.callback);
	}
	
	/*
	@Deprecated
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key >= 0 && keys[key] != null) {	// Key is negative when performing certain shortcuts.
			
			switch(action) {
			case 0:
				keys[key].onRelease();
				break;
			case 1:
				keys[key].onPress();
				break;
			case 2:
				if (keys[key].getState() != 2) {
					keys[key].onHold();
				}
				break;
			default:
					break;
			}
			keys[key].setState(action);
		}
	}	

	@Deprecated
	public void addKeyReleaseFunction(int key, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key();
		}
		keys[key].setReleaseAction(action);
	}
	
	@Deprecated
	public void addKeyPressFunction(int key, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key();
		}
		keys[key].setPressAction(action);
	}
*/

	@Override
	protected void notifyKeyPress(int key, Object arg) {
		for (KeyboardListener l : this.listeners) {
			l.notifyPress(key);
		}
	}


	@Override
	protected void notifyKeyRelease(int key, Object arg) {
		for (KeyboardListener l : this.listeners) {
			l.notifyRelease(key);
		}
	}


	@Override
	protected void notifyKeyHold(int key, Object arg) {
		for (KeyboardListener l : this.listeners) {
			l.notifyHold(key);
		}
	}
}
