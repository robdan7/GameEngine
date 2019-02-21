package core.input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import org.lwjgl.glfw.GLFW;

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
	 * 
	 * @param window - The window to bind the mouse to.
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
	
	public Vector2f getNormalizedPosition() {
		return new Vector2f((getX()/window.getWidth())*2-1,-getY()/window.getHeight()*(2)+1);
	}
	
	/**
	 * set delta x to null.
	 */
	private void setNullDelta() {
		this.lastX = this.getPosition().getX();
		this.lastY = this.getPosition().getY();
	}
	
	/**
	 * 
	 * @param windowWitdth - Width of the window.
	 * @return The mouse movement relative to the window width.
	 */
	public float getDX() {
		float DX = (float)(this.getPosition().getX()-this.lastX)/this.window.getWidth();
		this.lastX = this.getPosition().getX();
		return DX*this.movementRatio;
	}
	
	/**
	 * 
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
	 * @return The position in screen coordinates where 0 is in the center of the screen.
	 */
	public Vector2f getScreenPosition () {
		return new Vector2f(this.getPosition().getX()-window.getWidth()/2, window.getHeight()/2-this.getPosition().getY());
	}
	
	public void toggleHide() {
		super.toggleHide();
		if (this.isHidden()) {
			this.showCursor(this.window);
			
		} else  {
			this.hideCursor(this.window);
		}
		this.setNullDelta();
	}
	
	public void toggleGrab() {
		super.toggleGrab();
		if (!this.isGrabbed()) {
			this.showCursor(this.window);
		} else  {
			this.grabCursor(this.window);
		}
		this.setNullDelta();
	}
	
	private void hideCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	private void showCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	private void grabCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
}