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

	
	public void addKeyReleaseFunction(int key, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key();
		}
		keys[key].setReleaseAction(action);
	}
	
	public void addKeyPressFunction(int key, Runnable action) {
		if (keys[key] == null) {
			keys[key] = new Key();
		}
		keys[key].setPressAction(action);
	}
}
