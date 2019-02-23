package core.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import core.engine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends GLFWKeyCallback{
	
	private Key[] keys;
	
	public Keyboard(Window window) {
		keys = new Key[65536];
		glfwSetKeyCallback(window.getWindow(), this);		
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		// TODO Auto-generated method stub
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
	
	/**
	 * Add a command to specified key. Not more than one function is allowed per key.
	 * WARNING: THIS DOES NOTHING
	 * @param key - They key to bind a function to.
	 * @param action - A runnable method.
	 */
	@Deprecated
	public void addKeyFunction(int key, Runnable action) {
		//keys[key] = new Key(action);
	}
	
	/**
	 * Add a command to specified key. Not more than one function is allowed per key.
	 * @param key - They key to bind a function to.
	 * @param type - Press once or hold down key. True = press. False = hold.
	 * @param action - A runnable method.
	 */
	@Deprecated
	public void addKeyFunction(int key, Key.actionType type, Runnable action) {
		keys[key] = new Key(type, action);
	}
	
	public void addKeyReleaseFunction(int key, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key(null);
		}
		keys[key].setReleaseAction(action);
	}
	
	public void addKeyPressFunction(int key, Key.actionType type, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key(type);
		}
		keys[key].setPressAction(action);
	}
	
	
	/**
	 * Search for any pressed keys.
	 */
	@Deprecated
	public void getInput() {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null && keys[i].isPressed()) {
				keys[i].run();
				switch (keys[i].getType()) {
				case TYPE:
					keys[i].setState(0);
					break;
				case HOLD:
					break;
					
				default:
					break;
				}
				/*if (keys[i].getType()) {
					keys[i].setState(0);
				}*/
			}
		}
	}
	


}
