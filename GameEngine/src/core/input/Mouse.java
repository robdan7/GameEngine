package core.input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import core.engine.Window;
import core.input.listeners.MouseController;
import core.utils.math.Vector2f;

//Our MouseHandler class extends the abstract class
//abstract classes should never be instantiated so here
//we create a concrete that we can instantiate
public class Mouse extends MouseController{
	double lastX;
	double lastY;
	
	float movementRatio;
	Window window;
	GLFWCursorPosCallback callback;
	GLFWMouseButtonCallback buttonCallback;
	
	int leftClickState = 0;
	
	
	/**
	 * Create the mouse and connect it to a window.
	 * @param window - The window to bind the mouse to.
	 * @param ratio - Input to movement ratio.
	 */
	public Mouse(Window window, float ratio) {
		super();
		this.callback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long arg0, double x, double y) {
				setX((float)x);
				setY((float)y);
				notifyMouseMovement();
			}
		};
		this.buttonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int state, int arg3) {
				if(button == 1) {
					setRightClick(state == 1 ? true : false);
					notifyRightClick(state == 1 ? true : false);
				} else {
					setLeftClick(state == 1 ? true : false);
					notifyLeftClick();
				}
			}
		};
		this.window = window;
		glfwSetCursorPosCallback(this.window.getWindow(), this.callback);
		glfwSetMouseButtonCallback(this.window.getWindow(), this.buttonCallback);
		this.movementRatio = ratio;
	}
	
	/**
	 * set delta x to null.
	 */
	private void setNullDelta() {
		this.lastX = this.getPosition().getX();
		this.lastY = this.getPosition().getY();
}
	
	/**
	 * Get delta X since last call.
	 * @param windowWitdth - Width of the window.
	 * @return The mouse movement relative to the window width.
	 */
	public float getDX() {
		float DX = (float)(this.getPosition().getX()-this.lastX)/this.window.getWidth();
		this.lastX = this.getPosition().getX();
		return DX*this.movementRatio;
	}
	
	/**
	 * Get delta Y since last call.
	 * @param windowHeight - Height of the window.
	 * @return The mouse movement relative to the window height.
	 */
	public float getDY() {
		float DY = (float)(this.getPosition().getY()-this.lastY)/this.window.getHeight();
		this.lastY = this.getPosition().getY();
		return DY*this.movementRatio;
}
	
	/**
	 * 
	 * @return The position in screen coordinates. 0 is in the center of the screen.
	 */
	public Vector2f getScreenPosition () {
		return new Vector2f(this.getPosition().getX()-window.getWidth()/2, window.getHeight()/2-this.getPosition().getY());
	}
	
	/**
	 * 
	 * @return The position relative to the screen size.
	 */
	public Vector2f getNormalizedPosition() {
		return new Vector2f((getX()/window.getWidth())*2-1,-getY()/window.getHeight()*(2)+1);
	}
	
	/**
	 * Hide and show the mouse.
	 */
	public void toggleHide() {
		super.toggleHide();
		if (this.isHidden()) {
			this.showCursor(this.window);
			
		} else  {
			this.hideCursor(this.window);
		}
		this.setNullDelta();
	}
	
	/**
	 * Grab and release the mouse. The mouse cursor will not move or be shown when it is grabbed.
	 */
	public void toggleGrab() {
		super.toggleGrab();
		if (!this.isGrabbed()) {
			this.showCursor(this.window);
		} else  {
			this.grabCursor(this.window);
		}
		this.setNullDelta();
	}
	
	/**
	 * Hide the mouse cursor. The cursor can still move when it is hidden.
	 * @param window
	 */
	private void hideCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	/**
	 * Show the mouse cursor. This is pretty self explanatory.
	 * @param window
	 */
	private void showCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	/**
	 * Grab the mouse cursor. This disables cursor movement.
	 * @param window
	 */
	private void grabCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
}