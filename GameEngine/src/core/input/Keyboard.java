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
			if(keys[key].getType() == Key.actionType.TYPE && action == 2) {
				keys[key].setState(0);
			} else {
				keys[key].setState(action);
			}
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
	public void addKeyFunction(int key, Key.actionType type, Runnable action) {
		keys[key] = new Key(type, action);
	}
	
	
	/**
	 * Search for any pressed keys.
	 */
	public void getInput() {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null && keys[i].getPress()) {
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
